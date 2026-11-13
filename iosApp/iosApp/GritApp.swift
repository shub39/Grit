import Shared
import SwiftUI

@main
struct GritApp: App {
    init() {
        // Records the platform bridges; `MainViewControllerKt.MainViewController()` then starts the
        // Koin graph and the Compose UI.
        GritSdk.shared.configure(
            services: IosPlatformServices(
                alarms: LocalAlarmBridge(),
                notifications: LocalNotificationBridge(),
                billing: StubBillingBridge(),
                analytics: ConsoleAnalyticsBridge(),
                biometrics: LocalBiometricBridge()
            )
        )
    }

    var body: some Scene {
        WindowGroup {
            // Compose draws edge to edge and applies the safe area insets itself, exactly like
            // `enableEdgeToEdge()` does on Android.
            ComposeView()
                .ignoresSafeArea()
        }
    }
}
