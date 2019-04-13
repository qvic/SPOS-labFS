#!/usr/bin/python3
import argparse

parser = argparse.ArgumentParser()

parser.add_argument('files_number', type=int,
                    help='Number of files to be read')

parser.add_argument('--file', type=str, default="fs",
                    help='File where filesystem was saved')

args = parser.parse_args()

print("in " + args.file)

for x in range(1, args.files_number + 1):
    filename = "f{}".format(x)
    print("op " + filename)
    print("rd 1 192")
    print("cl 1")
