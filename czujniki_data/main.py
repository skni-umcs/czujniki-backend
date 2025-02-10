from influxdb_client import InfluxDBClient, Point
from influxdb_client.client.write_api import SYNCHRONOUS
import time
from datetime import datetime
import random

url = "http://localhost:8086" #URL DO ZMIANY NA PRD
token = "UunckRyjgp1adXfh-NMW4Fp3aXC8DcbeMPMScJcptT1qMj_e7A-gMtmc6JbzUdPkpOZp9MAwGaadCc3cje-MJw==" #TOKEN DO ZMIANY NA PRD
org = "SKNI"
bucket = "czujniki"

client = InfluxDBClient(url=url, token=token, org=org)
write_api = client.write_api(write_options=SYNCHRONOUS)


def generate_sensor_reading(sensor_id: int) -> dict:
    """
    rust template:
    struct SensorReading {
        time: DateTime<Utc>,
        sensor_id: i16,
        raw_temperature: i16,
        raw_humidity: u32,
        raw_pressure: u32,
        raw_gas_resistance: u32,
    }
    """
    return {
        "time": datetime.utcnow(),
        "sensor_id": sensor_id,
        "raw_temperature": random.randint(-400, 850) / 10,
        "raw_humidity": random.randint(0, 1000) / 10,
        "raw_pressure": random.randint(3000, 11000) / 10,
        "raw_gas_resistance": random.randint(100, 100000)
    }


def write_to_influx():

    for sensor_id in range(1, 6):
        reading = generate_sensor_reading(sensor_id)

        point = Point("sensor_readings") \
            .time(reading["time"]) \
            .field("sensor_id", reading["sensor_id"]) \
            .field("raw_temperature", reading["raw_temperature"]) \
            .field("raw_humidity", reading["raw_humidity"]) \
            .field("raw_pressure", reading["raw_pressure"]) \
            .field("raw_gas_resistance", reading["raw_gas_resistance"])

        write_api.write(bucket=bucket, record=point)
        print(f"Zapisano odczyt: {reading}")


def main():

    print("Rozpoczynam zapisywanie odczytów do sensor_readings...")
    try:
        while True:
            write_to_influx()
            print("\nCzekam 30 sekund przed następnymi odczytami...\n")
            time.sleep(30)
    except KeyboardInterrupt:
        print("\nZatrzymano program")
        client.close()


if __name__ == "__main__":
    main()