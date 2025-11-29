// Define the pins used for the power latch circuit
const int LATCH_PIN = 4;     // GPIO to control the NPN latch / to open the gate
const int BUTTON_PIN = 2;    // GPIO to read the button state

// Define how long to hold the button to power off (in milliseconds)
const long SHUTDOWN_HOLD_TIME = 3000; // 3 seconds

// Variables to track button hold time
unsigned long buttonPressTime = 0;
bool isShuttingDown = false;

void power_setup(){
  pinMode(LATCH_PIN, OUTPUT);
  pinMode(BUTTON_PIN, INPUT_PULLUP);
}