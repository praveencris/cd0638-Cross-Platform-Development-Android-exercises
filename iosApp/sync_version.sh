#!/bin/bash

# sync_version.sh
# Synchronizes marketing version from gradle.properties (single source of truth)
# to iOS Config.xcconfig. Build numbers are independent per store.

set -e  # Exit on error

# Color output for better visibility
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "${YELLOW}🔄 Syncing marketing version from gradle.properties to iOS...${NC}"

# Paths
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
GRADLE_PROPS="$PROJECT_ROOT/gradle.properties"
XCCONFIG="$PROJECT_ROOT/iosApp/Configuration/Config.xcconfig"

# Check if gradle.properties exists
if [ ! -f "$GRADLE_PROPS" ]; then
    echo "${RED}❌ Error: gradle.properties not found${NC}"
    exit 1
fi

# Check if Config.xcconfig exists
if [ ! -f "$XCCONFIG" ]; then
    echo "${RED}❌ Error: iosApp/Configuration/Config.xcconfig not found${NC}"
    exit 1
fi

# Extract version components from gradle.properties
MAJOR=$(grep -E '^APP_VERSION_MAJOR=' "$GRADLE_PROPS" | awk -F'=' '{print $2}' | tr -d ' ')
MINOR=$(grep -E '^APP_VERSION_MINOR=' "$GRADLE_PROPS" | awk -F'=' '{print $2}' | tr -d ' ')
PATCH=$(grep -E '^APP_VERSION_PATCH=' "$GRADLE_PROPS" | awk -F'=' '{print $2}' | tr -d ' ')

if [ -z "$MAJOR" ] || [ -z "$MINOR" ] || [ -z "$PATCH" ]; then
    echo "${RED}❌ Error: Could not extract APP_VERSION_MAJOR/MINOR/PATCH from gradle.properties${NC}"
    exit 1
fi

MARKETING_VERSION="$MAJOR.$MINOR.$PATCH"
echo "  📦 gradle.properties version: $MARKETING_VERSION"

# Read current CURRENT_PROJECT_VERSION (build number — independent, not synced)
CURRENT_BUILD=$(grep -E '^CURRENT_PROJECT_VERSION=' "$XCCONFIG" | awk -F'=' '{print $2}' | tr -d ' ')
echo "  🔢 Current iOS build number: $CURRENT_BUILD (independent, not synced)"

# Update MARKETING_VERSION in Config.xcconfig
sed -i '' "s/^MARKETING_VERSION=.*/MARKETING_VERSION=$MARKETING_VERSION/" "$XCCONFIG"

echo "${GREEN}✅ Config.xcconfig updated successfully${NC}"
echo "  🍎 MARKETING_VERSION: $MARKETING_VERSION (synced from gradle.properties)"
echo "  🔢 CURRENT_PROJECT_VERSION: $CURRENT_BUILD (unchanged — increment manually per App Store submission)"
