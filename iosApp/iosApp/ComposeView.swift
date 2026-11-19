import Shared
import SwiftUI
import UIKit

/// Hosts the Compose Multiplatform UI that is built from the shared Kotlin code.
///
/// `GritSdk.shared.start(services:)` has already run in `GritApp.init`, so the Koin graph the
/// viewmodels need is ready by the time this controller is created.
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
