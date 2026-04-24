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
        .package(path: "../../Core/CoreCommon"),
        .package(path: "../FeatureScribble"),
        .package(path: "../FeatureAI"),
        .package(path: "../FeatureConfig"),
        .package(path: "../FeatureSettings"),
        .package(path: "../FeatureInsights"),
        .package(path: "../FeatureSets"),
        .package(path: "../FeatureExercises")
    ],
    targets: [
        .target(
            name: "FeatureCanvas",
            dependencies: [
                .product(name: "CoreModel", package: "CoreModel"),
                .product(name: "CoreDatabase", package: "CoreDatabase"),
                .product(name: "CoreDesignSystem", package: "CoreDesignSystem"),
                .product(name: "CoreFirebase", package: "CoreFirebase"),
                .product(name: "CoreCommon", package: "CoreCommon"),
                .product(name: "FeatureScribble", package: "FeatureScribble"),
                .product(name: "FeatureAI", package: "FeatureAI"),
                .product(name: "FeatureConfig", package: "FeatureConfig"),
                .product(name: "FeatureSettings", package: "FeatureSettings"),
                .product(name: "FeatureInsights", package: "FeatureInsights"),
                .product(name: "FeatureSets", package: "FeatureSets"),
                .product(name: "FeatureExercises", package: "FeatureExercises")
            ]
        )
    ]
)
