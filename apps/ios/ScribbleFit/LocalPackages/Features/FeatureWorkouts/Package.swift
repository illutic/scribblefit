// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "FeatureWorkouts",
    platforms: [.iOS(.v17)],
    products: [
        .library(name: "FeatureWorkouts", targets: ["FeatureWorkouts"])
    ],
    dependencies: [
        .package(path: "../../Core/CoreModel"),
        .package(path: "../../Core/CoreDatabase")
    ],
    targets: [
        .target(
            name: "FeatureWorkouts",
            dependencies: [
                .product(name: "CoreModel", package: "CoreModel"),
                .product(name: "CoreDatabase", package: "CoreDatabase")
            ]
        )
    ]
)
