// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "FeatureSettings",
    platforms: [.iOS(.v17), .macOS(.v14)],
    products: [
        .library(name: "FeatureSettings", targets: ["FeatureSettings"])
    ],
    dependencies: [
        .package(path: "../../Core/CoreModel"),
        .package(path: "../../Core/CoreDatabase"),
        .package(path: "../../Core/CoreDesignSystem"),
        .package(path: "../../Core/CoreCommon"),
        .package(path: "../FeatureAI"),
        .package(path: "../FeatureConfig")
    ],
    targets: [
        .target(
            name: "FeatureSettings",
            dependencies: [
                .product(name: "CoreModel", package: "CoreModel"),
                .product(name: "CoreDatabase", package: "CoreDatabase"),
                .product(name: "CoreDesignSystem", package: "CoreDesignSystem"),
                .product(name: "CoreCommon", package: "CoreCommon"),
                .product(name: "FeatureAI", package: "FeatureAI"),
                .product(name: "FeatureConfig", package: "FeatureConfig")
            ]
        )
    ]
)
