#!/bin/bash

# sync_version.sh - OPTIONAL STRETCH GOAL
# Synchronizes marketing version from gradle.properties (single source of truth)
# to iOS Config.xcconfig. Build numbers are independent per store.
#
# NOTE: The primary exercise focuses on manual Xcode configuration.
# This script provides an optional automation approach for advanced students.

set -e  # Exit on error

# Color output for better visibility
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "${YELLOW}🔄 Syncing marketing version from gradle.properties to iOS...${NC}"

# TODO 1: Define paths to the files you need
# Hint: Use $(cd "$(dirname "$0")/.." && pwd) to get the project root
# You need:
# - PROJECT_ROOT: The root directory of the project
# - GRADLE_PROPS: Path to gradle.properties (single source of truth)
# - XCCONFIG: Path to iosApp/Configuration/Config.xcconfig

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
GRADLE_PROPS="$PROJECT_ROOT/gradle.properties"
XCCONFIG="$PROJECT_ROOT/iosApp/Configuration/Config.xcconfig"

# TODO 2: Verify required files exist
# Check if GRADLE_PROPS and XCCONFIG files exist
# Exit with error message if either is missing

if [ ! -f "$GRADLE_PROPS" ]; then
    echo "${RED}❌ Error: gradle.properties not found${NC}"
    exit 1
fi

if [ ! -f "$XCCONFIG" ]; then
    echo "${RED}❌ Error: iosApp/Configuration/Config.xcconfig not found${NC}"
    exit 1
fi

# TODO 3: Extract version components from gradle.properties
# Hint: Use grep to find lines like APP_VERSION_MAJOR=1
# Then use awk to extract the value after '='
# Example: grep -E '^APP_VERSION_MAJOR=' "$GRADLE_PROPS" | awk -F'=' '{print $2}'
#
# You need three variables: MAJOR, MINOR, PATCH

MAJOR=""  # Replace with your extraction logic
MINOR=""  # Replace with your extraction logic
PATCH=""  # Replace with your extraction logic
# Your code here

if [ -z "$MAJOR" ] || [ -z "$MINOR" ] || [ -z "$PATCH" ]; then
    echo "${RED}❌ Error: Could not extract APP_VERSION_MAJOR/MINOR/PATCH from gradle.properties${NC}"
    exit 1
fi

MARKETING_VERSION="$MAJOR.$MINOR.$PATCH"
echo "  📦 gradle.properties version: $MARKETING_VERSION"

# Read current build number (independent, not synced)
CURRENT_BUILD=$(grep -E '^CURRENT_PROJECT_VERSION=' "$XCCONFIG" | awk -F'=' '{print $2}' | tr -d ' ')
echo "  🔢 Current iOS build number: $CURRENT_BUILD (independent, not synced)"

# TODO 4: Update MARKETING_VERSION in Config.xcconfig
# Hint: Use sed to replace the MARKETING_VERSION line
# Example: sed -i '' "s/^MARKETING_VERSION=.*/MARKETING_VERSION=$MARKETING_VERSION/" "$XCCONFIG"
#
# IMPORTANT: Only update MARKETING_VERSION, NOT CURRENT_PROJECT_VERSION
# Build numbers are independent per store — iOS increments its own build number
# with each App Store submission

# Your code here

echo "${GREEN}✅ Config.xcconfig updated successfully${NC}"
echo "  🍎 MARKETING_VERSION: $MARKETING_VERSION (synced from gradle.properties)"
echo "  🔢 CURRENT_PROJECT_VERSION: $CURRENT_BUILD (unchanged — increment manually per App Store submission)"
