import Foundation
import LocalAuthentication
import Shared
import UserNotifications

/// Reminders for habits (repeating) and tasks/subtasks (one shot).
final class LocalAlarmBridge: NSObject, IosAlarmBridge {
    func schedule(reminder: IosReminder) {
        guard let trigger = makeTrigger(for: reminder) else { return }

        let content = UNMutableNotificationContent()
        content.title = reminder.title
        content.body = reminder.body
        content.sound = .default

        UNUserNotificationCenter.current().add(
            UNNotificationRequest(identifier: reminder.id, content: content, trigger: trigger)
        )
    }

    func cancel(id: String) {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [id])
        UNUserNotificationCenter.current().removeDeliveredNotifications(withIdentifiers: [id])
    }

    func cancelAll() {
        UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
    }

    private func makeTrigger(for reminder: IosReminder) -> UNNotificationTrigger? {
        if let fireAt = reminder.fireAtEpochMillis?.int64Value {
            let date = Date(timeIntervalSince1970: TimeInterval(fireAt) / 1000)
            let components = Calendar.current.dateComponents(
                [.year, .month, .day, .hour, .minute],
                from: date
            )
            return UNCalendarNotificationTrigger(dateMatching: components, repeats: false)
        }

        guard let weekday = reminder.weekdays.first?.int32Value else { return nil }

        var components = DateComponents()
        components.weekday = Int(weekday)
        components.hour = Int(reminder.hour)
        components.minute = Int(reminder.minute)
        return UNCalendarNotificationTrigger(dateMatching: components, repeats: true)
    }
}

/// Short lived notifications, e.g. after completing a habit.
final class LocalNotificationBridge: NSObject, IosNotificationBridge {
    func show(id: String, title: String, body: String) {
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body

        UNUserNotificationCenter.current().add(
            UNNotificationRequest(identifier: id, content: content, trigger: nil)
        )
    }

    func cancel(id: String) {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [id])
        UNUserNotificationCenter.current().removeDeliveredNotifications(withIdentifiers: [id])
    }
}

/// Placeholder until StoreKit 2 is wired up: the app behaves like the FOSS build, so nothing is
/// gated behind a paywall that cannot be bought yet.
final class StubBillingBridge: NSObject, IosBillingBridge {
    func isPlusUser() -> Bool {
        true
    }

    func isFoss() -> Bool {
        true
    }
}

final class ConsoleAnalyticsBridge: NSObject, IosAnalyticsBridge {
    func track(event: String, properties: [String: String]) {
        print("[analytics] \(event) \(properties)")
    }
}

final class LocalBiometricBridge: NSObject, IosBiometricBridge {
    func getAuthenticators() -> Int32 {
        let context = LAContext()
        var error: NSError?
        guard context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) else {
            return 0
        }
        return context.biometryType == .faceID ? 2 : 1
    }

    func authenticationAvailable() -> Bool {
        LAContext().canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: nil)
    }

    func authenticate(reason: String, onResult: @escaping (KotlinBoolean) -> Void) {
        let context = LAContext()
        context.evaluatePolicy(.deviceOwnerAuthentication, localizedReason: reason) { success, _ in
            // Compose state is updated from the callback, so it has to land on the main thread.
            DispatchQueue.main.async { onResult(KotlinBoolean(bool: success)) }
        }
    }
}
