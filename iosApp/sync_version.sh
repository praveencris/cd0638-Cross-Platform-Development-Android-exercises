#!/bin/bash

# sync_version.sh
# Synchronizes Android version (single source of truth) to iOS Info.plist
# This prevents version drift between platforms during production releases

set -e  # Exit on error

# Color output for better visibility
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "${YELLOW}🔄 Syncing Android version to iOS...${NC}"

# Paths
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
BUILD_GRADLE="$PROJECT_ROOT/composeApp/build.gradle.kts"
INFO_PLIST="$PROJECT_ROOT/iosApp/iosApp/Info.plist"

# Check if PlistBuddy is available (macOS only)
if ! command -v /usr/libexec/PlistBuddy &> /dev/null; then
    echo "${RED}❌ Error: PlistBuddy not found. This script requires macOS.${NC}"
    exit 1
fi

# Check if build.gradle.kts exists
if [ ! -f "$BUILD_GRADLE" ]; then
    echo "${RED}❌ Error: composeApp/build.gradle.kts not found${NC}"
    exit 1
fi

# Check if Info.plist exists
if [ ! -f "$INFO_PLIST" ]; then
    echo "${RED}❌ Error: iosApp/iosApp/Info.plist not found${NC}"
    exit 1
fi

# Extract versionCode from build.gradle.kts
VERSION_CODE=$(grep -E 'versionCode\s*=\s*[0-9]+' "$BUILD_GRADLE" | awk -F'=' '{print $2}' | tr -d ' ')
if [ -z "$VERSION_CODE" ]; then
    echo "${RED}❌ Error: Could not extract versionCode from build.gradle.kts${NC}"
    exit 1
fi

# Extract versionName from build.gradle.kts
VERSION_NAME=$(grep -E 'versionName\s*=\s*"' "$BUILD_GRADLE" | awk -F'"' '{print $2}')
if [ -z "$VERSION_NAME" ]; then
    echo "${RED}❌ Error: Could not extract versionName from build.gradle.kts${NC}"
    exit 1
fi

echo "  📱 Android versionCode: $VERSION_CODE"
echo "  📱 Android versionName: $VERSION_NAME"

# Update Info.plist using PlistBuddy
# CFBundleVersion = versionCode (build number)
# CFBundleShortVersionString = versionName (user-facing version)

/usr/libexec/PlistBuddy -c "Set :CFBundleVersion $VERSION_CODE" "$INFO_PLIST" 2>/dev/null || \
/usr/libexec/PlistBuddy -c "Add :CFBundleVersion string $VERSION_CODE" "$INFO_PLIST"

/usr/libexec/PlistBuddy -c "Set :CFBundleShortVersionString $VERSION_NAME" "$INFO_PLIST" 2>/dev/null || \
/usr/libexec/PlistBuddy -c "Add :CFBundleShortVersionString string $VERSION_NAME" "$INFO_PLIST"

echo "${GREEN}✅ iOS Info.plist updated successfully${NC}"
echo "  🍎 CFBundleVersion: $VERSION_CODE"
echo "  🍎 CFBundleShortVersionString: $VERSION_NAME"
