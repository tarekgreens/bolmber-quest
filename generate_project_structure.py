#!/usr/bin/env python3

import os
import sys

# Configuration
OUTPUT_FILE_FULLPATH = 'ProjectFilesFullPath.txt'  # Output file to store the list of files
OUTPUT_FILE_STRUCTURE = 'ProjectStructure.txt'      # Output file for the tree-like structure
BASE_DIR = '.'                                     # Base directory (current directory)

# File extensions to include (excluding .ico)
INCLUDE_EXTENSIONS = (
    '.java',        # C# source files
    '.gradle',    # C# project files
    '.sln',       # Solution files
    '.json',      # JSON configuration files    # JavaScript files
)

# Files to exclude
EXCLUDE_FILES = {
    'ScaffoldingReadMe.txt',
    'AggregatedProject.txt',
    'NecessaryCommands.txt',
    'ProjectFilesFullPath.txt',
    'generate_project_structure.py',
    'ProjectStructureForGenerativeAI.txt',
    'ProjectStructure.txt',
    'bootstrap.min.css'
}

# Directories to exclude (and all their contents)
EXCLUDE_DIRS = {
    'obj',
    'Migrations',
    os.path.join('wwwroot', 'js', 'lib'),   # 'wwwroot/js/lib'
    os.path.join('wwwroot', 'lib'),        # 'wwwroot/lib'
    'bin'
}

def is_included_file(file_name):
    """
    Determines if a file should be included based on its extension and exclusion list.
    """
    if file_name in EXCLUDE_FILES:
        return False
    _, ext = os.path.splitext(file_name)
    return ext.lower() in INCLUDE_EXTENSIONS

def should_exclude_dir(dir_path, base_dir):
    """
    Determines if a directory should be excluded based on the exclusion list.
    """
    # Get the relative path of the directory from base_dir
    rel_dir_path = os.path.relpath(dir_path, base_dir)
    rel_dir_path = rel_dir_path.replace(os.sep, '/')
    # Check if the relative directory path matches any exclusion
    for excluded in EXCLUDE_DIRS:
        # Normalize paths to ensure consistent comparison
        excluded_norm = os.path.normpath(excluded)
        rel_dir_norm = os.path.normpath(rel_dir_path)
        if rel_dir_norm == excluded_norm:
            return True
    return False

def build_tree_structure(file_paths):
    """
    Builds a nested dictionary representing the directory tree from a list of file paths.
    """
    tree = {}
    for path in file_paths:
        parts = path.split('/')
        current_level = tree
        for part in parts[:-1]:  # Traverse directories
            current_level = current_level.setdefault(part, {})
        current_level.setdefault(parts[-1], None)  # Add file
    return tree

def write_tree(structure, output_file, prefix=''):
    """
    Writes the tree structure to the output file using tree-like characters.
    """
    entries = sorted(structure.keys())
    for index, entry in enumerate(entries):
        is_last = index == len(entries) - 1
        connector = '└── ' if is_last else '├── '
        output_file.write(f"{prefix}{connector}{entry}\n")
        if isinstance(structure[entry], dict):
            extension = '    ' if is_last else '│   '
            write_tree(structure[entry], output_file, prefix + extension)

def generate_project_structure(base_dir, output_file_fullpath, output_file_structure):
    """
    Traverses the base directory recursively, writes included file paths to ProjectFilesFullPath.txt,
    and writes the tree-like structure to ProjectStructure.txt.
    """
    included_files = []

    with open(output_file_fullpath, 'w', encoding='utf-8') as outfile_fullpath:
        for root, dirs, files in os.walk(base_dir):
            # Modify dirs in-place to exclude certain directories
            dirs[:] = [d for d in dirs if not should_exclude_dir(os.path.join(root, d), base_dir)]
            for file in files:
                if is_included_file(file):
                    # Construct the relative file path
                    file_path = os.path.relpath(os.path.join(root, file), base_dir)
                    # Normalize the path to use forward slashes
                    file_path = file_path.replace(os.sep, '/')
                    included_files.append(file_path)
                    outfile_fullpath.write(f"{file_path}\n")

    print(f"Project files have been written to '{output_file_fullpath}'.")

    # Build the tree structure
    tree = build_tree_structure(included_files)

    # Write the tree structure to ProjectStructure.txt
    with open(output_file_structure, 'w', encoding='utf-8') as outfile_structure:
        outfile_structure.write(".\n")
        write_tree(tree, outfile_structure)

    print(f"Project structure has been written to '{output_file_structure}'.")

def main():
    # Check if the base directory exists
    if not os.path.isdir(BASE_DIR):
        print(f"Error: The base directory '{BASE_DIR}' does not exist.")
        sys.exit(1)
    
    # Check if the output files already exist to prevent accidental exclusion
    if os.path.exists(os.path.join(BASE_DIR, OUTPUT_FILE_FULLPATH)):
        print(f"Warning: '{OUTPUT_FILE_FULLPATH}' already exists and will be overwritten.")
    if os.path.exists(os.path.join(BASE_DIR, OUTPUT_FILE_STRUCTURE)):
        print(f"Warning: '{OUTPUT_FILE_STRUCTURE}' already exists and will be overwritten.")
    
    generate_project_structure(BASE_DIR, OUTPUT_FILE_FULLPATH, OUTPUT_FILE_STRUCTURE)

if __name__ == '__main__':
    main()
