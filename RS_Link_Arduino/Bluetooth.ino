#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <BLE2902.h>



#define SERVICE_UUID        "12345678-1234-5678-1234-56789abcdef0"
#define CHARACTERISTIC_UUID "abcdef01-1234-5678-1234-56789abcdef0"

BLEServer* pServer = NULL;
BLECharacteristic* pCharacteristic = NULL;

bool deviceConnected = false;   // a flag to check if it is connected

#include <Wire.h>
#include <MPU6050.h>

const int CRASH_BUTTON_PIN = 5;

bool buzzerOn = false;
bool falseAlarmSent = false;

unsigned long lastToneTime = 0;
bool toneState = false;

MPU6050 mpu;

// Thresholds
// 4.0g for testing, 3.5G for more sensitivity
const float CRASH_THRESHOLD = 4.0; // 4G trigger (adjust based on testing)
const float TILT_THRESHOLD = 0.5;  // If Z-axis is below 0.5g, it's tipped over

unsigned long crashTime = 0;
bool crashDetected = false;
const long CRASH_CONFIRM_TIME = 12000; // 12 seconds
bool crashConfirmed = false;

const long required_crash_button_hold = 2000;
const long required_manual_crash_button_hold = 5000;
bool crashButtonHold = false;
unsigned long crashButtonPressTime = 0;

bool buttonActionTaken = false;

bool crashAlertSend = false;

class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
      deviceConnected = true;
      Serial.println("Device connected");
     digitalWrite(BUZZER_PIN, HIGH);
      delay(50); // Beep 1 (Short)
      digitalWrite(BUZZER_PIN, LOW);
      delay(50); // Gap 1

      digitalWrite(BUZZER_PIN, HIGH);
      delay(50); // Beep 2 (Short)
      digitalWrite(BUZZER_PIN, LOW);
      delay(50); // Gap 2

      digitalWrite(BUZZER_PIN, HIGH);
      delay(50); // Beep 3 (Short, quick final tap)
      digitalWrite(BUZZER_PIN, LOW);
    }

    void onDisconnect(BLEServer* pServer) {
      deviceConnected = false;
      digitalWrite(BUZZER_PIN, HIGH);
      delay(50); // Beep 1 (Short)
      digitalWrite(BUZZER_PIN, LOW);
      delay(50); // Gap 1

      digitalWrite(BUZZER_PIN, HIGH);
      delay(50); // Beep 2 (Short)
      digitalWrite(BUZZER_PIN, LOW);
      delay(50); // Gap 2

      digitalWrite(BUZZER_PIN, HIGH);
      delay(150); // Beep 3 (Longer final confirmation)
      digitalWrite(BUZZER_PIN, LOW);
      delay(200);
      Serial.println("Device disconnected");
      // Restart advertising to be connectable again
      pServer->startAdvertising(); //opens the connection to be connect to it again
    }
};

// Define callback class before using it
class MyCallbacks: public BLECharacteristicCallbacks {
   void onWrite(BLECharacteristic *pCharacteristic) {
      String value = String(pCharacteristic->getValue().c_str());
      
      if (value.length() > 0) {
        Serial.println();
        for (int i = 0; i < value.length(); i++) {
          Serial.print(value[i]);

            // // This will detect if the sent message matches these
            // if (value[0] == 'B') {  // activates the buzzer pin
            //   tone(BUZZER_PIN, 1000);  // set the buzzer frequency to 1000 Hz tone
            //   buzzerOn = true;  // set the flag that says the buzzer is turned on
            //   falseAlarmSent = false; 
            //   Serial.println("Buzzer turned ON via BLE");
            // }

            // else if (value[0] == 'S') {
            //   noTone(BUZZER_PIN);      // Stop the tone
            //   buzzerOn = false;
            //   Serial.println("Buzzer turned OFF via BLE");
            // }
        }
        Serial.println();
        
        // Echo back
        pCharacteristic->setValue(value);
        pCharacteristic->notify();
      } 
      else {
        Serial.println("Received empty data");
      }
    }
     void onRead(BLECharacteristic *pCharacteristic) {
      Serial.println("onRead called!");
    }

    // void onNotify(BLECharacteristic *pCharacteristic) {
    //   Serial.println("onNotify called!");
    // }
};

void bluetooth_setup() {
   pinMode(CRASH_BUTTON_PIN, INPUT_PULLUP);
   Wire.begin(6, 7);
  
  mpu.initialize();

  // Set sensor ranges (MUST match these in calculations)
  mpu.setFullScaleAccelRange(MPU6050_ACCEL_FS_16);  // ±16g 
    
  BLEDevice::init("ESP32_C6_MPU6050"); // set the name to be detected
  pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks()); 

  // Create the BLE Service
  BLEService *pService = pServer->createService(SERVICE_UUID);
  Serial.print("Created service with UUID: ");
  Serial.println(SERVICE_UUID);

  pCharacteristic = pService->createCharacteristic(
      CHARACTERISTIC_UUID,
      BLECharacteristic::PROPERTY_READ |
      BLECharacteristic::PROPERTY_WRITE | 
      BLECharacteristic::PROPERTY_NOTIFY
  );

  Serial.print("Created characteristic with UUID: ");
  Serial.println(CHARACTERISTIC_UUID);

  // Add descriptor
  pCharacteristic->addDescriptor(new BLE2902());
  
  // Set callbacks
  pCharacteristic->setCallbacks(new MyCallbacks());
  
  // Start the service
  pService->start();
  Serial.println("Service started");

  // 1. Get the Advertising Object
  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();

  // 2. IMPORTANT: Add the Service UUID to the advertisement
  // Without this, your Android ScanFilter will ignore the device!
  pAdvertising->addServiceUUID(SERVICE_UUID);

  // 3. Set visual settings (Optional but recommended)
  pAdvertising->setScanResponse(true);
  pAdvertising->setMinPreferred(0x06);  // functions that help with iPhone connections issue
  pAdvertising->setMaxPreferred(0x12);

  // 4. Start Advertising
  BLEDevice::startAdvertising();

  Serial.println("BLE Initialized. Waiting for connections...");

 
}

void bluetooth_loop() {

  
  if (buzzerOn) {
    if(millis() - crashTime >= CRASH_CONFIRM_TIME){
      crashConfirmed = true;
    }

    // Buzzer sounds for crash
    if(crashConfirmed){
      // Sound for confirmed crash
      if (millis() - lastToneTime >= 200) {
        if (toneState) {
          tone(BUZZER_PIN, 1800, 150);
        }
        else 
        {
          tone(BUZZER_PIN, 1800, 300);
        }
        toneState = !toneState;
        lastToneTime = millis(); // IMPORTANT: Update timer logic
      }

      // Send an alert through bluetooth
      if(crashAlertSend == false){
        String stop = "Crash Confirmed";
        pCharacteristic->setValue(stop.c_str());
        pCharacteristic->notify(); // Sends data to Android
        Serial.println("Confirmed Crash Alert Sent");
        crashAlertSend = true;
      }
      Serial.println("Crash Confirmed!!!");
    }
    else{
      // Sound for detected crash 
      if (millis() - lastToneTime >= 300) { // Change pulse interval here
        lastToneTime = millis();
        if (toneState) {
          noTone(BUZZER_PIN);
        } else {
          tone(BUZZER_PIN, 1000, 150);
        }
        toneState = !toneState;
      }
      Serial.println("Crash Detected!!!");
    }

  } 
  else {
    noTone(BUZZER_PIN); // Ensure it's off
  }
  // Crash button
  if (digitalRead(CRASH_BUTTON_PIN) == LOW){
    if(crashButtonPressTime == 0){
      crashButtonPressTime = millis(); // set the holding time
    }
    else if (millis() - crashButtonPressTime > required_crash_button_hold){
      if(!buzzerOn && !crashConfirmed && !buttonActionTaken){
        crashConfirmed = true;
        buzzerOn = true;
        crashAlertSend = true;
        String manualAlert = "Manual Alert";
        pCharacteristic->setValue(manualAlert.c_str());
        pCharacteristic->notify(); 
        Serial.println(manualAlert);
        
        buttonActionTaken = true; // Lock the button so it doesn't trigger "Stop" immediately
      }
      // SCENARIO 2: Stop Alert (Manually cancelling)
      // Added "!buttonActionTaken" here too
      else if (crashConfirmed == true && !buttonActionTaken){
        String stop = "Confirmed Accident"; // Or "Alert Cancelled"
        pCharacteristic->setValue(stop.c_str());
        pCharacteristic->notify(); 
        
        crashConfirmed = false;
        buzzerOn = false;
        crashAlertSend = false;
        Serial.println("Stopped via button");
        
        buttonActionTaken = true; // Lock the button
      }
      // SCENARIO C: Stop False Detection (The "Warning" Phase) <--- PASTE IT HERE
      else if (crashConfirmed == false && buzzerOn == true && !buttonActionTaken){
        buzzerOn = false;
        Serial.println("Stopped at crash detection");
        
        buttonActionTaken = true; // IMPORTANT: Lock the button here too!
      }
    }
  }
  else{ // reset the holding state of the button
    if (crashButtonPressTime > 0) {
      // Button was just released
      Serial.println("Button released.");
      crashButtonPressTime = 0; // Reset the timer
      buttonActionTaken = false; // Reset the lock
    }
  }
  int16_t ax, ay, az;
    mpu.getAcceleration(&ax, &ay, &az);

    // 1. Convert accelerometer (Range is now ±16g, so divide by 2048.0)
    float ax_g = ax / 2048.0; 
    float ay_g = ay / 2048.0;
    float az_g = az / 2048.0;

    // 2. Calculate Total Acceleration Vector (Magnitude)
    // We subtract 1.0 to account for gravity, looking only at "extra" force
    float total_g = sqrt(sq(ax_g) + sq(ay_g) + sq(az_g));

    // --- CRASH DETECTION LOGIC ---
    
    // Step 1: Look for the Impact Spike
    if (total_g > CRASH_THRESHOLD && !crashDetected) {
        Serial.println("IMPACT DETECTED! Waiting to confirm...");
        crashTime = millis();
        crashDetected = true;
    }

    // Step 2: Confirmation (2 seconds after impact)
    if (crashDetected && (millis() - crashTime >= 2000)) {
        
        // Check if the device is tilted (Z-axis is no longer vertical)
        // Normal riding: Z is close to 1.0 or -1.0. Tipped over: Z is close to 0.
        if (abs(az_g) < TILT_THRESHOLD) {
            Serial.println("CRASH CONFIRMED: High G + Tilt Detected");
            // Trigger Buzzer
            buzzerOn = true;
            
             // Send "C" for Crash to Android App
             String crashMsg = "CRASH_DETECTED";
             pCharacteristic->setValue(crashMsg.c_str());
             pCharacteristic->notify();
        } else {
             Serial.println("False Alarm: Impact detected but device is upright.");
        }
        
        // Reset logic to scan for next crash
        crashDetected = false;
    }
// Format data strings
    String sensorData1 = "A:" + 
        String(ax_g, 3) + "," + 
        String(ay_g, 3) + "," + 
        String(az_g, 3);



  // Send data in a simple CSV format
  // Serial.println(
  //     String(ax_g, 3) + "," + 
  //     String(ay_g, 3) + "," + 
  //     String(az_g, 3));

  //   // Debug output
  //  Serial.println(sensorData1);
    
    delay(20); // ~50Hz sampling (better than 500ms)

    
}
