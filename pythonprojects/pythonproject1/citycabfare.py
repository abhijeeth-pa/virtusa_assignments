vehicle_rates={
    'Economy':10,
    'Premium':18,
    'SUV':25
}

surge_start=17
surge_end=20
surge_increase=1.5

def calculate_fare(km:float,vehicle_type:str,hour:int) -> int:
    if vehicle_type not in vehicle_rates:
        return ValueError("Invalid vehicle type.choose from available vehicle types:{','.join(vehicle_rates.keys())}")
    if km<=0:
        return ValueError("Distance must be greater than zero.")
    if hour<0 or hour>23:
        return ValueError("Hour must be between 0 and 23.")
    
    rate=vehicle_rates[vehicle_type]
    base_fare=km*rate
    is_surge=surge_start<=hour<surge_end
    surge_increment=surge_increase if is_surge else 1.0
    total_fare=base_fare*surge_increment
    return total_fare

if __name__=="__main__":
    try:
        km=float(input("Enter distance in kilometers: "))
        vehicle_type=input("Enter vehicle type (Economy, Premium, SUV): ")
        hour=int(input("Enter hour of the day (0-23): "))
        fare=calculate_fare(km,vehicle_type,hour)
        print("="*50)
        print("City Cab Fare Calculator")
        print("="*50)
        print(f"Distance: {km} km")
        print(f"Vehicle Type: {vehicle_type}")
        print(f"Hour of the Day: {hour}")
        print(f"The total fare for your ride is: {fare:.2f}")
        print("="*50)
    except ValueError as e:
        print(e)