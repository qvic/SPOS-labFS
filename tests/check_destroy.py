#!/usr/bin/python3
import argparse

parser = argparse.ArgumentParser()

parser.add_argument('files_number', type=int,
                    help='Number of files to be created')

parser.add_argument('--file', type=str, default="fs",
                    help='File where filesystem will be saved')

args = parser.parse_args()

# fill disk

for x in range(1, args.files_number + 1):
    filename = "f{}".format(x)
    print("cr " + filename)
    print("op " + filename)
    print("wr 1 a 192")
    print("cl 1")

print("de f1")

print("cr f19")
print("op f19")
print("wr 1 b 192")
print("cl 1")

# print all

for x in range(1, args.files_number + 1):
    filename = "f{}".format(x + 1)
    print("op " + filename)
    print("rd 1 192")
    print("cl 1")
