// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "FeatureCanvas",
    platforms: [.iOS(.v17)],
    products: [
        .library(name: "FeatureCanvas", targets: ["FeatureCanvas"])
    ],
    dependencies: [
        .package(path: "../../Core/CoreModel"),
        .package(path: "../../Core/CoreDatabase"),
        .package(path: "../../Core/CoreDesignSystem"),
        .package(path: "../../Core/CoreFirebase"),
        .package(path: "../FeatureScribble"),
        .package(path: "../FeatureWorkouts"),
        .package(path: "../FeatureAI"),
        .package(path: "../FeatureConfig"),
        .package(path: "../FeatureSettings"),
        .package(path: "../FeatureInsights"),
        .package(path: "../FeatureLedger"),
        .package(path: "../FeatureSets")
    ],
    targets: [
        .target(
            name: "FeatureCanvas",
            dependencies: [
                .product(name: "CoreModel", package: "CoreModel"),
                .product(name: "CoreDatabase", package: "CoreDatabase"),
                .product(name: "CoreDesignSystem", package: "CoreDesignSystem"),
                .product(name: "CoreFirebase", package: "CoreFirebase"),
                .product(name: "FeatureScribble", package: "FeatureScribble"),
                .product(name: "FeatureWorkouts", package: "FeatureWorkouts"),
                .product(name: "FeatureAI", package: "FeatureAI"),
                .product(name: "FeatureConfig", package: "FeatureConfig"),
                .product(name: "FeatureSettings", package: "FeatureSettings"),
                .product(name: "FeatureInsights", package: "FeatureInsights"),
                .product(name: "FeatureLedger", package: "FeatureLedger"),
                .product(name: "FeatureSets", package: "FeatureSets")
            ]
        )
    ]
)
