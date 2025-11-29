
const int BUZZER_PIN = 3;



void setup() {
  power_setup();
  bluetooth_setup();

}

void loop() {
  power_loop();
  bluetooth_loop();
}

