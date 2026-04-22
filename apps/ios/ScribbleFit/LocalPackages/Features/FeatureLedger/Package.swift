// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "FeatureLedger",
    platforms: [.iOS(.v17)],
    products: [
        .library(name: "FeatureLedger", targets: ["FeatureLedger"])
    ],
    dependencies: [
        .package(path: "../../Core/CoreModel"),
        .package(path: "../../Core/CoreDesignSystem"),
        .package(path: "../../Core/CoreCommon"),
        .package(path: "../FeatureWorkouts"),
        .package(path: "../FeatureExercises")
    ],
    targets: [
        .target(
            name: "FeatureLedger",
            dependencies: [
                .product(name: "CoreModel", package: "CoreModel"),
                .product(name: "CoreDesignSystem", package: "CoreDesignSystem"),
                .product(name: "CoreCommon", package: "CoreCommon"),
                .product(name: "FeatureWorkouts", package: "FeatureWorkouts"),
                .product(name: "FeatureExercises", package: "FeatureExercises")
            ]
        )
    ]
)
