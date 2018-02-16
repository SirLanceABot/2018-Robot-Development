import json
l = ['Wait', 'MoveForTime', 'Shoot', 'Target', 'SpinForTime', 'SpinToDegree', 'LowerShooter', 'RaiseShooter']
with open("commands.json", "w+") as j:
    json.dump(l, j, sort_keys=True)