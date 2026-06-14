// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "FeatureConfig",
    platforms: [.iOS(.v17), .macOS(.v14)],
    products: [
        .library(name: "FeatureConfig", targets: ["FeatureConfig"])
    ],
    dependencies: [
        .package(path: "../../Core/CoreModel"),
        .package(path: "../../Core/CoreFirebase")
    ],
    targets: [
        .target(
            name: "FeatureConfig",
            dependencies: [
                .product(name: "CoreModel", package: "CoreModel"),
                .product(name: "CoreFirebase", package: "CoreFirebase")
            ]
        ),
        .testTarget(
            name: "FeatureConfigTests",
            dependencies: [
                "FeatureConfig",
                .product(name: "CoreModel", package: "CoreModel")
            ],
            path: "Tests/FeatureConfigTests"
        )
    ]
)
