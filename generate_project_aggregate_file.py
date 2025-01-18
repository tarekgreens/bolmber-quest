#!/usr/bin/env python3

import os
import sys

# Configuration
PROJECT_FILES = 'ProjectFilesFullPath.txt'   # Input file with list of project files
OUTPUT_FILE = 'AggregatedProject.txt'        # Output aggregated file
BASE_DIR = '.'                                # Base directory (current directory)

def aggregate_files(project_files, output_file, base_dir):
    """
    Aggregates the content of all specified files into a single output file with file references.
    """
    if not os.path.isfile(project_files):
        print(f"Error: '{project_files}' does not exist in '{base_dir}'.")
        sys.exit(1)
    
    total_files = 0
    aggregated_files = 0
    missing_files = 0
    with open(project_files, 'r', encoding='utf-8') as infile, \
         open(output_file, 'w', encoding='utf-8') as outfile:
        for line_number, line in enumerate(infile, start=1):
            file_path = line.strip()
            if not file_path:
                continue  # Skip empty lines
            total_files += 1
            full_path = os.path.join(base_dir, file_path)
            if not os.path.isfile(full_path):
                print(f"Warning: '{file_path}' does not exist or is not a file. (Line {line_number})")
                missing_files += 1
                continue
            # Write a header for each file
            outfile.write(f"\n\n--- File: {file_path} ---\n\n")
            try:
                with open(full_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    outfile.write(content)
                print(f"Successfully aggregated: {file_path}")
                aggregated_files += 1
            except Exception as e:
                print(f"Error reading '{file_path}': {e}")
    
    print("\n=== Aggregation Summary ===")
    print(f"Total files listed: {total_files}")
    print(f"Files successfully aggregated: {aggregated_files}")
    print(f"Files missing or failed to aggregate: {missing_files}")
    print(f"\nAggregation complete. Output file: '{output_file}'")

def main():
    # Check if the base directory exists
    if not os.path.isdir(BASE_DIR):
        print(f"Error: The base directory '{BASE_DIR}' does not exist.")
        sys.exit(1)
    
    # Check if the output file already exists to prevent accidental overwriting
    if os.path.exists(os.path.join(BASE_DIR, OUTPUT_FILE)):
        print(f"Warning: '{OUTPUT_FILE}' already exists and will be overwritten.")
    
    aggregate_files(PROJECT_FILES, OUTPUT_FILE, BASE_DIR)

if __name__ == '__main__':
    main()
