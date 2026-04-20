// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "CoreDesignSystem",
    platforms: [.iOS(.v17)],
    products: [
        .library(name: "CoreDesignSystem", targets: ["CoreDesignSystem"])
    ],
    dependencies: [
        .package(path: "../CoreModel")
    ],
    targets: [
        .target(
            name: "CoreDesignSystem",
            dependencies: [
                .product(name: "CoreModel", package: "CoreModel")
            ],
            resources: [
                .process("Resources")
            ]
        )
    ]
)
