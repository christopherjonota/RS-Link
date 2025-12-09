// Define the pins used for the power latch circuit
const int LATCH_PIN = 4;     // GPIO to control the NPN latch / to open the gate
const int POWER_BUTTON_PIN = 2;    // GPIO to read the button state

// Define how long to hold the button to power off (in milliseconds)
const long SHUTDOWN_HOLD_TIME = 3000; // 3 seconds

// Variables to track button hold time
unsigned long buttonPressTime = 0;
bool isShuttingDown = false;


void power_setup(){

  // Set the LATCH_PIN as an output and set it HIGH immediately
  // to engage the latch and keep the power on.
  pinMode(LATCH_PIN, OUTPUT);

      // Set the POWER_BUTTON_PIN as an input with an internal pull-up resistor.
  // The pull-up keeps the pin HIGH until the button is pressed,
  // which pulls it LOW to ground.
  pinMode(POWER_BUTTON_PIN, INPUT_PULLUP);

  long startTime = millis();  

  const int requiredTime = 3000; // 3 seconds in milliseconds is needed to hold in order to open the latch

  bool buttonHeld = true; // holds the state of the button from the start of the device to open

  // This loop runs while the user is holding the button (and thus powering the chip)
  while (buttonHeld) {
    
    // Check if the button is still pressed (assuming LOW when pressed with PULLUP)
    if (digitalRead(POWER_BUTTON_PIN) == LOW) { 
      
      // Check if the required time has passed
      if (millis() - startTime >= requiredTime) {
        
        digitalWrite(LATCH_PIN, HIGH);  // Open the latch

        break; // Exit the loop and continue running the rest of setup()
      }
    } else {

      // Button was released before 3 seconds
      buttonHeld = false; //reset the state of the button

      // The ESP32 automatically powers OFF here because the latch was never engaged.
    }

    // Small delay to prevent the watchdog timer from resetting the chip
    delay(50);
  }


  // Once the latch is open, this section will run
  
  pinMode(BUZZER_PIN, OUTPUT);
  digitalWrite(BUZZER_PIN, LOW); // Make sure it's off by default

  // Power on beeping
  // Beep the buzzer to signal "ON"
  digitalWrite(BUZZER_PIN, HIGH); 
  delay(50); // Short first beep
  digitalWrite(BUZZER_PIN, LOW); 
  delay(50); // Short pause
  digitalWrite(BUZZER_PIN, HIGH);
  delay(100); // Slightly longer second beep
  digitalWrite(BUZZER_PIN, LOW);


  // ------------------------------------------
  // Initialize Serial for debugging (optional)
  Serial.begin(115200);
}

void power_loop(){
// If we are already shutting down, do nothing else.
  // The power will be cut when the user releases the button.

  if (isShuttingDown) {
    return;
  }
 // Read the current state of the button
  int buttonState = digitalRead(POWER_BUTTON_PIN);

  if (buttonState == LOW) { // Button is being pressed
    
    if (buttonPressTime == 0) { // This is the first moment the press was detected
      buttonPressTime = millis();
      Serial.println("Button pressed...");
    } 
    else if (millis() - buttonPressTime > SHUTDOWN_HOLD_TIME) { // detects if the button pressed for the required shutdown time
      digitalWrite(BUZZER_PIN, HIGH);
      delay(400); // Beep for half a second
      digitalWrite(BUZZER_PIN, LOW);
      delay(100);
      digitalWrite(BUZZER_PIN, HIGH);
      delay(200);
       // Beep for half a second
      digitalWrite(BUZZER_PIN, LOW);
      // Button has been held long enough to trigger shutdown
      shutdown();
    }
  } else {
    // Button is not pressed (state is HIGH)
    if (buttonPressTime > 0) {
      // Button was just released
      Serial.println("Button released.");
      buttonPressTime = 0; // Reset the timer
    }
  }

}

void shutdown() {
  isShuttingDown = true;
  Serial.println("Shutting down! Release button to cut power.");

  // Release the latch!
  digitalWrite(LATCH_PIN, LOW);

  // Enter an infinite loop. The CPU will "freeze" here.
  // Power will be cut by the hardware (R1) as soon
  // as the user releases the button.
  while (true) {
    // Wait for the power to be cut
  }
}