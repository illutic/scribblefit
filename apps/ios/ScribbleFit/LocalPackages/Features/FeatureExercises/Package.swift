// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "FeatureExercises",
    platforms: [.iOS(.v17), .macOS(.v14)],
    products: [
        .library(name: "FeatureExercises", targets: ["FeatureExercises"])
    ],
    dependencies: [
        .package(path: "../../Core/CoreModel"),
        .package(path: "../../Core/CoreDatabase"),
        .package(path: "../../Core/CoreDesignSystem"),
        .package(path: "../../Core/CoreCommon"),
        .package(path: "../FeatureAI"),
        .package(path: "../FeatureScribble")
    ],
    targets: [
        .target(
            name: "FeatureExercises",
            dependencies: [
                .product(name: "CoreModel", package: "CoreModel"),
                .product(name: "CoreDatabase", package: "CoreDatabase"),
                .product(name: "CoreDesignSystem", package: "CoreDesignSystem"),
                .product(name: "CoreCommon", package: "CoreCommon"),
                .product(name: "FeatureAI", package: "FeatureAI"),
                .product(name: "FeatureScribble", package: "FeatureScribble")
            ]
        ),
        .testTarget(
            name: "FeatureExercisesTests",
            dependencies: [
                "FeatureExercises",
                .product(name: "CoreModel", package: "CoreModel"),
                .product(name: "CoreCommon", package: "CoreCommon")
            ],
            path: "Tests/FeatureExercisesTests"
        )
    ]
)
