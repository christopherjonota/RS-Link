
const int BUZZER_PIN = 3;



void setup() {
  // --- THIS MUST BE THE VERY FIRST THING YOU DO ---
  // Set the LATCH_PIN as an output and set it HIGH immediately
  // to engage the latch and keep the power on.
  
  
    // Set the BUTTON_PIN as an input with an internal pull-up resistor.
  // The pull-up keeps the pin HIGH until the button is pressed,
  // which pulls it LOW to ground.
  

  long startTime = millis();  
  const int requiredTime = 3000; // 3 seconds in milliseconds
  bool buttonHeld = true;

  // This loop runs while the user is holding the button (and thus powering the chip)
  while (buttonHeld) {
    
    // Check if the button is still pressed (assuming LOW when pressed with PULLUP)
    if (digitalRead(BUTTON_PIN) == LOW) { 
      
      // Check if the required time has passed
      if (millis() - startTime >= requiredTime) {
        
        // 3. LATCH COMMAND (SUCCESS!)
        digitalWrite(LATCH_PIN, HIGH);
        break; // Exit the loop and continue running the rest of setup()
      }
    } else {
      // Button was released before 3 seconds
      buttonHeld = false;
      // The ESP32 automatically powers OFF here because the latch was never engaged.
    }

    // Small delay to prevent the watchdog timer from resetting the chip
    delay(50);
  }




  // ------------------------------------------------
  // --- ADD THIS SECTION FOR THE BUZZER ---
  
  pinMode(BUZZER_PIN, OUTPUT);
  digitalWrite(BUZZER_PIN, LOW); // Make sure it's off by default

  // Beep the buzzer to signal "ON"
  digitalWrite(BUZZER_PIN, HIGH); // Turn buzzer on
  delay(100);                     // Beep for 100 milliseconds (0.1 sec)
  digitalWrite(BUZZER_PIN, LOW);  // Turn buzzer off
  // ------------------------------------------
  // Initialize Serial for debugging (optional)
  Serial.begin(115200);
  Serial.println("System ON. Latch engaged.");


}

void loop() {
  // If we are already shutting down, do nothing else.
  // The power will be cut when the user releases the button.

  if (isShuttingDown) {
    return;
  }

  // Read the current state of the button
  int buttonState = digitalRead(BUTTON_PIN);

  if (buttonState == LOW) {
    // Button is being pressed
    if (buttonPressTime == 0) {
      // This is the first moment the press was detected
      buttonPressTime = millis();
      Serial.println("Button pressed...");
    } else if (millis() - buttonPressTime > SHUTDOWN_HOLD_TIME) {
      digitalWrite(BUZZER_PIN, HIGH); // Turn buzzer on
      delay(100);                     // Beep for 100 milliseconds (0.1 sec)
      digitalWrite(BUZZER_PIN, LOW);  // Turn buzzer off
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
  
  // --- Optional: Add any cleanup code here ---
  // (e.g., save data to preferences, close files)
  // ------------------------------------------

  // Release the latch!
  digitalWrite(LATCH_PIN, LOW);

  // Enter an infinite loop. The CPU will "freeze" here.
  // Power will be cut by the hardware (R1) as soon
  // as the user releases the button.
  while (true) {
    // Wait for the power to be cut
  }
}