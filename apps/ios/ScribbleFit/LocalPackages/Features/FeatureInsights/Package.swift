// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "FeatureInsights",
    platforms: [.iOS(.v17)],
    products: [
        .library(name: "FeatureInsights", targets: ["FeatureInsights"])
    ],
    dependencies: [
        .package(path: "../../Core/CoreModel"),
        .package(path: "../../Core/CoreDesignSystem"),
        .package(path: "../FeatureAI")
    ],
    targets: [
        .target(
            name: "FeatureInsights",
            dependencies: [
                .product(name: "CoreModel", package: "CoreModel"),
                .product(name: "CoreDesignSystem", package: "CoreDesignSystem"),
                .product(name: "FeatureAI", package: "FeatureAI")
            ]
        )
    ]
)
