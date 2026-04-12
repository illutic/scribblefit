// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "ScribbleFit",
    platforms: [.iOS(.v17), .macOS(.v14)],
    products: [
        .library(name: "ScribbleFit", targets: [
            "FeatureCanvas", "FeatureSettings", "FeatureInsights", "FeatureLedger"
        ])
    ],
    dependencies: [],
    targets: [
        // Core
        .target(name: "CoreModel", path: "Sources/Core/Model"),
        .target(name: "CoreDatabase", dependencies: ["CoreModel"], path: "Sources/Core/Database"),
        .target(name: "CoreDesignSystem", dependencies: ["CoreModel"], path: "Sources/Core/DesignSystem"),
        .target(name: "CoreCommon", path: "Sources/Core/Common"),

        // Features (no UI)
        .target(name: "FeatureAI", dependencies: ["CoreModel", "CoreCommon"],
                path: "Sources/Features/AI"),
        .target(name: "FeatureScribble", dependencies: ["CoreModel", "CoreDatabase", "FeatureAI"],
                path: "Sources/Features/Scribble"),
        .target(name: "FeatureWorkouts", dependencies: ["CoreModel", "CoreDatabase"],
                path: "Sources/Features/Workouts"),
        .target(name: "FeatureConfig", dependencies: ["CoreModel"],
                path: "Sources/Features/Config"),

        // Features (with UI)
        .target(name: "FeatureCanvas", dependencies: [
            "CoreModel", "CoreDatabase", "CoreDesignSystem",
            "FeatureScribble", "FeatureWorkouts", "FeatureAI", "FeatureConfig",
            "FeatureSettings", "FeatureInsights", "FeatureLedger"
        ], path: "Sources/Features/Canvas"),
        .target(name: "FeatureSettings", dependencies: [
            "CoreModel", "CoreDatabase", "CoreDesignSystem", "CoreCommon",
            "FeatureAI", "FeatureConfig"
        ], path: "Sources/Features/Settings"),
        .target(name: "FeatureInsights", dependencies: [
            "CoreModel", "CoreDesignSystem",
            "FeatureWorkouts", "FeatureAI"
        ], path: "Sources/Features/Insights"),
        .target(name: "FeatureLedger", dependencies: [
            "CoreModel", "CoreDesignSystem", "FeatureWorkouts"
        ], path: "Sources/Features/Ledger"),

        // Tests
        .testTarget(name: "ScribbleFitTests", dependencies: [
            "FeatureCanvas", "FeatureSettings", "FeatureInsights", "FeatureLedger",
            "CoreModel", "CoreDatabase", "FeatureAI", "FeatureScribble",
            "FeatureWorkouts", "FeatureConfig"
        ], path: "ScribbleFitTests"),
    ]
)
