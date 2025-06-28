#!/usr/bin/env python3

"""
@file build_native_libs.py
@brief Builds native C++ shared libraries for chess and poker using CMake and Ninja.

This script automates the process of:
- removing build directories,
- running CMake configuration,
- compiling the project using Ninja,
- and moving the resulting `.so` file into the `lib/` folder.
"""

import shutil
import subprocess

from enum import Enum
from pathlib import Path
from typing import List

class Colour(Enum):
    """
    @enum Colour

    @extends Enum

    @brief Enum representing ANSI terminal colours.
    """
    RED = "\033[31m"
    GREEN = "\033[32m"
    YELLOW = "\033[33m"
    RESET = "\033[0m"

class Project(Enum):
    """
    @enum Project
    
    @extends Enum

    @brief Enum for available native C++ projects.
    """
    CHESS = "chess"
    POKER = "poker"

def colour(text: str, colour_code: Colour) -> str:
    """
    @brief Wrap text in ANSI colour codes.

    @param text The text to colour.
    @param colour_code The Colour enum value to apply.
    @return A string wrapped with ANSI escape codes for terminal colourization.
    """
    return f"{colour_code.value}{text}{Colour.RESET.value}"

def runCommand(command: List[str], cwd: Path) -> None:
    """
    @brief Run a shell command in a given directory.

    @param command The command to execute, split into arguments.
    @param cwd The working directory to run the command in.
    
    @throws CalledProcessError if the command fails.
    """
    subprocess.run(command, cwd = cwd, check = True)

def buildProject(project: Project) -> None:
    """
    @brief Build a native C++ project and move its compiled `.so` file.

    @param project The project to build (e.g., Project.CHESS).
    """
    name: str = project.value
    base_dir: Path = Path("app/src/native") / name
    build_dir: Path = base_dir / "build"
    lib_name: Path = f"lib{name}.so"
    output_path: Path = base_dir / "lib" / lib_name

    print(f"{colour("Removing", Colour.RED)} build directory for {name}")
    shutil.rmtree(build_dir, ignore_errors = True)

    print(f"{colour("Initialising", Colour.GREEN)} {name} build")
    runCommand(["cmake", "-S", ".", "-G", "Ninja", "-B", "build"], cwd = base_dir)

    print(f"{colour("Building", Colour.GREEN)} {name}")
    runCommand(["cmake", "--build", "build"], cwd = base_dir)

    print(f"{colour("Moving", Colour.GREEN)} {lib_name} to lib directory")
    built_so: Path = build_dir / "lib" / lib_name
    output_path.parent.mkdir(parents = True, exist_ok = True)
    shutil.move(str(built_so), str(output_path))

    print(colour("Build complete!", Colour.GREEN))

def main() -> None:
    """
    @brief Main entry point that builds all native libraries.
    """
    for project in Project:
        buildProject(project)

if __name__ == "__main__":
    main()
