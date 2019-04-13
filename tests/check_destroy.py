#!/usr/bin/python3
import argparse

parser = argparse.ArgumentParser()

parser.add_argument('--file', type=str, default="fs",
                    help='File where filesystem will be saved')

args = parser.parse_args()

print("in " + args.file)

print("de f1")

print("cr f19")
print("op f19")
print("wr 1 b 192")

print("sv " + args.file)
