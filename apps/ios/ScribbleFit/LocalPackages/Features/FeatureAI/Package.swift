// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "FeatureAI",
    platforms: [.iOS(.v17)],
    products: [
        .library(name: "FeatureAI", targets: ["FeatureAI"])
    ],
    dependencies: [
        .package(path: "../../Core/CoreModel"),
        .package(path: "../../Core/CoreCommon"),
        .package(path: "../../Core/CoreFirebase")
    ],
    targets: [
        .target(
            name: "FeatureAI",
            dependencies: [
                .product(name: "CoreModel", package: "CoreModel"),
                .product(name: "CoreCommon", package: "CoreCommon"),
                .product(name: "CoreFirebase", package: "CoreFirebase")
            ]
        )
    ]
)
