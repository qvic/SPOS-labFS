#!/usr/bin/python3

import sys

files_number = int(sys.argv[1])
max_bytes_in_file = int(sys.argv[2])

print("in fs")

for x in range(1, files_number + 1):
    filename = "f{}".format(x)
    print("op " + filename)
    print("rd 1 {}".format(max_bytes_in_file))
    print("cl 1")