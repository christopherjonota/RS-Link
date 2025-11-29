#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <Wire.h>
#include <MPU6050.h>
#include <BLE2902.h>



bool buzzerOn = false;
bool falseAlarmSent = false;

unsigned long lastToneTime = 0;
bool toneState = false;

MPU6050 mpu;

#define SERVICE_UUID        "12345678-1234-5678-1234-56789abcdef0"
#define CHARACTERISTIC_UUID "abcdef01-1234-5678-1234-56789abcdef0"


// Calibration offsets (run calibration routine to get these)
const float GYRO_OFFSET_X = 0.01;
const float GYRO_OFFSET_Y = -0.02;
const float GYRO_OFFSET_Z = 0.03;

// Normalization factor for ±250°/s range
const float GYRO_NORM_FACTOR = 0.000133f; // (250 * PI/180) / 32768

BLEServer* pServer = NULL;
BLECharacteristic* pCharacteristic = NULL;
bool deviceConnected = false;

class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
      deviceConnected = true;
      Serial.println("Device connected");
    }

    void onDisconnect(BLEServer* pServer) {
      deviceConnected = false;
      Serial.println("Device disconnected");
      // Restart advertising to be connectable again
      pServer->startAdvertising();
    }
};

// Define callback class before using it
class MyCallbacks: public BLECharacteristicCallbacks {
   void onWrite(BLECharacteristic *pCharacteristic) {
      String value = String(pCharacteristic->getValue().c_str());
      
      // Serial.print("onWrite called! Value length: ");
      // Serial.println(value.length());
      
      if (value.length() > 0) {
        // Serial.print("Data as HEX: ");
        // for (int i = 0; i < value.length(); i++) {
        //   Serial.print(value[i], HEX);
        //   Serial.print(" ");
        // }
        Serial.println();
        for (int i = 0; i < value.length(); i++) {
          Serial.print(value[i]);
            if (value[0] == 'B') {
              tone(buzzerPin, 1000);  // 1000 Hz tone
              buzzerOn = true;
              falseAlarmSent = false;
              Serial.println("Buzzer turned ON via BLE");
            } else if (value[0] == 'S') {
              noTone(buzzerPin);      // Stop the tone
              buzzerOn = false;
              Serial.println("Buzzer turned OFF via BLE");
            }
        }
        Serial.println();
        
        // Echo back
        pCharacteristic->setValue(value);
        pCharacteristic->notify();
      } else {
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
   Wire.begin(6, 7);
  
    mpu.initialize();




// Set sensor ranges (MUST match these in calculations)
    mpu.setFullScaleAccelRange(MPU6050_ACCEL_FS_2);  // ±2g
    mpu.setFullScaleGyroRange(MPU6050_GYRO_FS_250);  // ±250°/s

 // ***********************
    // CALIBRATION PROCEDURE
    // ***********************
    Serial.println("Place sensor on FLAT surface and don't move it!");
    delay(3000); // Give user time to position the device
    
    calibrateGyro(); 

    BLEDevice::init("ESP32_C6_MPU6050");
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


    // Start advertising
  pServer->getAdvertising()->start();



    Serial.println("BLE Initialized. Waiting for connections...");
}

void bluetooth_loop() {
  
  if (buzzerOn) {
    if (millis() - lastToneTime >= 300) { // Change pulse interval here
      lastToneTime = millis();
      if (toneState) {
        noTone(buzzerPin);
      } else {
        tone(buzzerPin, 1000);
      }
      toneState = !toneState;
    }
  } else {
    noTone(buzzerPin); // Ensure it's off
  }
   // Check for physical false alarm button press
  if (buzzerOn && digitalRead(buttonPin) == LOW && !falseAlarmSent) {
    // Debounce delay
    delay(200);

    // Confirm button is still pressed
    if (digitalRead(buttonPin) == LOW) {
      digitalWrite(buzzerPin, LOW);
      buzzerOn = false;
      falseAlarmSent = true;

      // Notify Android of false alarm
      Serial.println("S");
      String stop = "S";
      pCharacteristic->setValue(stop.c_str());
    pCharacteristic->notify(); // Sends data to Android
    }
  }
   int16_t ax, ay, az, gx, gy, gz;
    mpu.getAcceleration(&ax, &ay, &az);
    mpu.getRotation(&gx, &gy, &gz);

    // 1. Convert accelerometer (to g units)
    float ax_g = ax / 16384.0;  // ±2g = 32768/2
    float ay_g = ay / 16384.0;
    float az_g = az / 16384.0;

    // 2. Normalize gyroscope values
    float gx_norm = (gx * GYRO_NORM_FACTOR) - GYRO_OFFSET_X;
    float gy_norm = (gy * GYRO_NORM_FACTOR) - GYRO_OFFSET_Y;
    float gz_norm = (gz * GYRO_NORM_FACTOR) - GYRO_OFFSET_Z;

    // Format data strings
    String sensorData1 = "A:" + 
        String(ax_g, 3) + "," + 
        String(ay_g, 3) + "," + 
        String(az_g, 3);
    
    String sensorData2 = "G:" + 
        String(gx_norm, 3) + "," + 
        String(gy_norm, 3) + "," + 
        String(gz_norm, 3);

    pCharacteristic->setValue(sensorData1.c_str());
    pCharacteristic->notify(); // Sends data to Android
    delay(20);
    pCharacteristic->setValue(sensorData2.c_str());
    pCharacteristic->notify(); // Sends data to Android

    // Send data in a simple CSV format
    Serial.println(
        String(ax_g, 3) + "," + 
        String(ay_g, 3) + "," + 
        String(az_g, 3) + "," + 
        String(gx_norm, 3) + "," + 
        String(gy_norm, 3) + "," + 
        String(gz_norm, 3));

    // Debug output
   // Serial.println(sensorData1);
    //Serial.println(sensorData2);
    
    delay(20); // ~50Hz sampling (better than 500ms)
}

void calibrateGyro() {
    Serial.println("Calibrating gyro... keep sensor still");
    delay(1000);
    
    float gx_sum = 0, gy_sum = 0, gz_sum = 0;
    const int samples = 100;
    
    for (int i = 0; i < samples; i++) {
        int16_t gx, gy, gz;
        mpu.getRotation(&gx, &gy, &gz);
        gx_sum += gx * GYRO_NORM_FACTOR;
        gy_sum += gy * GYRO_NORM_FACTOR;
        gz_sum += gz * GYRO_NORM_FACTOR;
        delay(10);
    }
    
    Serial.print("Calculated offsets: ");
    Serial.print(gx_sum / samples, 4);
    Serial.print(", ");
    Serial.print(gy_sum / samples, 4);
    Serial.print(", ");
    Serial.println(gz_sum / samples, 4);
}