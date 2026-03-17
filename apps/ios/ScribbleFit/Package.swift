// swift-tools-version: 6.0
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "ScribbleFit",
    platforms: [
        .iOS("26.0"),
        .macOS("26.0")
    ],
    products: [
        .library(
            name: "ScribbleFit",
            targets: ["ScribbleFit"]
        ),
    ],
    dependencies: [
        // Add future dependencies here (e.g., SwiftData extensions or AI parsing clients)
    ],
    targets: [
        .target(
            name: "ScribbleFit",
            dependencies: [],
            path: "Sources"
        ),
    ]
)
