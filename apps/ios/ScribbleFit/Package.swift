// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "ScribbleFit",
    platforms: [.iOS(.v17), .macOS(.v14)],
    products: [
        .library(name: "ScribbleFit", targets: ["ScribbleFitAggregator"])
    ],
    dependencies: [
        .package(path: "LocalPackages/Core/CoreModel"),
        .package(path: "LocalPackages/Core/CoreDatabase"),
        .package(path: "LocalPackages/Core/CoreDesignSystem"),
        .package(path: "LocalPackages/Core/CoreCommon"),
        .package(path: "LocalPackages/Core/CoreFirebase"),
        .package(path: "LocalPackages/Features/FeatureAI"),
        .package(path: "LocalPackages/Features/FeatureScribble"),
        .package(path: "LocalPackages/Features/FeatureConfig"),
        .package(path: "LocalPackages/Features/FeatureCanvas"),
        .package(path: "LocalPackages/Features/FeatureSettings"),
        .package(path: "LocalPackages/Features/FeatureInsights"),
        .package(path: "LocalPackages/Features/FeatureLedger"),
        .package(path: "LocalPackages/Features/FeatureSets"),
        .package(path: "LocalPackages/Features/FeatureExercises")
    ],
    targets: [
        .target(
            name: "ScribbleFitAggregator",
            dependencies: [
                .product(name: "CoreModel", package: "CoreModel"),
                .product(name: "CoreDatabase", package: "CoreDatabase"),
                .product(name: "CoreDesignSystem", package: "CoreDesignSystem"),
                .product(name: "CoreCommon", package: "CoreCommon"),
                .product(name: "CoreFirebase", package: "CoreFirebase"),
                .product(name: "FeatureAI", package: "FeatureAI"),
                .product(name: "FeatureScribble", package: "FeatureScribble"),
                .product(name: "FeatureConfig", package: "FeatureConfig"),
                .product(name: "FeatureCanvas", package: "FeatureCanvas"),
                .product(name: "FeatureSettings", package: "FeatureSettings"),
                .product(name: "FeatureInsights", package: "FeatureInsights"),
                .product(name: "FeatureLedger", package: "FeatureLedger"),
                .product(name: "FeatureSets", package: "FeatureSets"),
                .product(name: "FeatureExercises", package: "FeatureExercises")
            ],
            path: "Sources/ScribbleFitAggregator"
        ),
    ]
)
