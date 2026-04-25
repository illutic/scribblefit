// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "CoreCommon",
    platforms: [.iOS(.v17), .macOS(.v14)],
    products: [
        .library(name: "CoreCommon", targets: ["CoreCommon"])
    ],
    targets: [
        .target(name: "CoreCommon")
    ]
)
