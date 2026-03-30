import SwiftUI
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        print("Firebase initialized on iOS")
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}