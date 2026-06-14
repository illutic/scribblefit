// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "FeatureScribble",
    platforms: [.iOS(.v17), .macOS(.v14)],
    products: [
        .library(name: "FeatureScribble", targets: ["FeatureScribble"])
    ],
    dependencies: [
        .package(path: "../../Core/CoreModel"),
        .package(path: "../../Core/CoreDatabase"),
        .package(path: "../FeatureAI")
    ],
    targets: [
        .target(
            name: "FeatureScribble",
            dependencies: [
                .product(name: "CoreModel", package: "CoreModel"),
                .product(name: "CoreDatabase", package: "CoreDatabase"),
                .product(name: "FeatureAI", package: "FeatureAI")
            ]
        ),
        .testTarget(
            name: "FeatureScribbleTests",
            dependencies: [
                "FeatureScribble",
                .product(name: "CoreModel", package: "CoreModel")
            ],
            path: "Tests/FeatureScribbleTests"
        )
    ]
)
