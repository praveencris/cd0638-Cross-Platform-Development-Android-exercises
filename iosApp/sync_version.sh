#!/bin/bash

# sync_version.sh - OPTIONAL STRETCH GOAL
# Synchronizes Android version (single source of truth) to iOS Info.plist
# This prevents version drift between platforms during production releases
#
# NOTE: The primary exercise focuses on manual Xcode configuration.
# This script provides an optional automation approach for advanced students.

set -e  # Exit on error

# Color output for better visibility
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "${YELLOW}🔄 Syncing Android version to iOS...${NC}"

# TODO 1: Define paths to the files you need
# Hint: Use $(cd "$(dirname "$0")/.." && pwd) to get the project root
# You need:
# - PROJECT_ROOT: The root directory of the project
# - BUILD_GRADLE: Path to composeApp/build.gradle.kts
# - INFO_PLIST: Path to iosApp/iosApp/Info.plist

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
BUILD_GRADLE="$PROJECT_ROOT/composeApp/build.gradle.kts"
INFO_PLIST="$PROJECT_ROOT/iosApp/iosApp/Info.plist"

# TODO 2: Verify PlistBuddy is available (macOS only tool)
# Hint: Use 'command -v /usr/libexec/PlistBuddy' to check
# Exit with error message if not found

if ! command -v /usr/libexec/PlistBuddy &> /dev/null; then
    echo "${RED}❌ Error: PlistBuddy not found. This script requires macOS.${NC}"
    exit 1
fi

# TODO 3: Verify required files exist
# Check if BUILD_GRADLE and INFO_PLIST files exist
# Exit with error message if either is missing

if [ ! -f "$BUILD_GRADLE" ]; then
    echo "${RED}❌ Error: composeApp/build.gradle.kts not found${NC}"
    exit 1
fi

if [ ! -f "$INFO_PLIST" ]; then
    echo "${RED}❌ Error: iosApp/iosApp/Info.plist not found${NC}"
    exit 1
fi

# TODO 4: Extract versionCode from build.gradle.kts
# Hint: Use grep to find the line with 'versionCode ='
# Then use awk or sed to extract just the number
# Example: grep -E 'versionCode\s*=\s*[0-9]+' "$BUILD_GRADLE" | awk ...

VERSION_CODE=""  # Replace with your extraction logic
# Your code here

if [ -z "$VERSION_CODE" ]; then
    echo "${RED}❌ Error: Could not extract versionCode from build.gradle.kts${NC}"
    exit 1
fi

# TODO 5: Extract versionName from build.gradle.kts
# Hint: Similar to above but look for 'versionName ='
# The value is in quotes, so you'll need to extract the string between quotes
# Example: grep -E 'versionName\s*=\s*"' "$BUILD_GRADLE" | awk -F'"' ...

VERSION_NAME=""  # Replace with your extraction logic
# Your code here

if [ -z "$VERSION_NAME" ]; then
    echo "${RED}❌ Error: Could not extract versionName from build.gradle.kts${NC}"
    exit 1
fi

echo "  📱 Android versionCode: $VERSION_CODE"
echo "  📱 Android versionName: $VERSION_NAME"

# TODO 6: Update Info.plist using PlistBuddy
# You need to set two keys:
# - CFBundleVersion = versionCode (build number)
# - CFBundleShortVersionString = versionName (user-facing version)
#
# PlistBuddy syntax:
#   /usr/libexec/PlistBuddy -c "Set :KeyName value" "$INFO_PLIST"
#   If the key doesn't exist, use "Add :KeyName string value" instead
#
# Hint: Use || to try Set first, and if it fails, use Add:
#   /usr/libexec/PlistBuddy -c "Set :CFBundleVersion $VERSION_CODE" "$INFO_PLIST" 2>/dev/null || \
#   /usr/libexec/PlistBuddy -c "Add :CFBundleVersion string $VERSION_CODE" "$INFO_PLIST"

# Your code here to update CFBundleVersion

# Your code here to update CFBundleShortVersionString

echo "${GREEN}✅ iOS Info.plist updated successfully${NC}"
echo "  🍎 CFBundleVersion: $VERSION_CODE"
echo "  🍎 CFBundleShortVersionString: $VERSION_NAME"
