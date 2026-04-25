// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "FeatureSets",
    platforms: [.iOS(.v17), .macOS(.v14)],
    products: [
        .library(name: "FeatureSets", targets: ["FeatureSets"])
    ],
    dependencies: [
        .package(path: "../../Core/CoreModel")
    ],
    targets: [
        .target(
            name: "FeatureSets",
            dependencies: [
                .product(name: "CoreModel", package: "CoreModel")
            ]
        )
    ]
)
