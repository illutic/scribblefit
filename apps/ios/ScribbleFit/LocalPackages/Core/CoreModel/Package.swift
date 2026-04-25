// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "CoreModel",
    platforms: [.iOS(.v17), .macOS(.v14)],
    products: [
        .library(name: "CoreModel", targets: ["CoreModel"])
    ],
    targets: [
        .target(name: "CoreModel")
    ]
)
