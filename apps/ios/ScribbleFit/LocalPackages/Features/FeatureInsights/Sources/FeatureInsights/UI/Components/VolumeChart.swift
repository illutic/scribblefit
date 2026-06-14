import SwiftUI
import CoreModel
import CoreDesignSystem

struct VolumeChart: View {
    let points: [VolumeDataPoint]
    let weightUnitLabel: String

    private static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM d"
        return formatter
    }()

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            GeometryReader { _ in
                Canvas { context, size in
                    if points.isEmpty { return }

                    let maxVolume = points.map { $0.volume }.max() ?? 0
                    let minVolume = points.map { $0.volume }.min() ?? 0
                    let range = max(maxVolume - minVolume, 1)

                    let yAxisWidth: CGFloat = 40
                    let xAxisHeight: CGFloat = 20
                    let dotRadius: CGFloat = 4.5
                    let haloRadius: CGFloat = 6.5
                    let strokeWidth: CGFloat = 2.5

                    let chartLeft = yAxisWidth
                    let chartRight = size.width - 8
                    let chartTop: CGFloat = 12
                    let chartBottom = size.height - xAxisHeight
                    let chartWidth = max(chartRight - chartLeft, 1)
                    let chartHeight = max(chartBottom - chartTop, 1)

                    func pointOffset(_ index: Int) -> CGPoint {
                        let x = points.count > 1
                            ? chartLeft + CGFloat(index) / CGFloat(points.count - 1) * chartWidth
                            : chartLeft + chartWidth / 2

                        let normalizedY = maxVolume > minVolume
                            ? CGFloat((points[index].volume - minVolume) / range)
                            : 0.5

                        let y = chartBottom - normalizedY * chartHeight
                        return CGPoint(x: x, y: y)
                    }

                    // Draw Y-axis labels (Max, Mid, Min)
                    let labelFont = Font.system(size: 10)
                    let midVolume = (maxVolume + minVolume) / 2
                    let yLabels = maxVolume > minVolume
                        ? [(maxVolume, chartTop), (midVolume, chartTop + chartHeight / 2), (minVolume, chartBottom)]
                        : [(maxVolume, chartTop + chartHeight / 2)]

                    for (value, y) in yLabels {
                        let text = Text(formatAxisValue(value))
                            .font(labelFont)
                            .foregroundColor(.secondary)

                        context.draw(text, at: CGPoint(x: yAxisWidth / 2, y: y), anchor: .center)
                    }

                    if points.count >= 2 {
                        var linePath = Path()
                        let first = pointOffset(0)
                        linePath.move(to: first)

                        for i in 1..<points.count {
                            let prev = pointOffset(i - 1)
                            let curr = pointOffset(i)
                            let cp1 = CGPoint(x: prev.x + (curr.x - prev.x) / 2, y: prev.y)
                            let cp2 = CGPoint(x: prev.x + (curr.x - prev.x) / 2, y: curr.y)
                            linePath.addCurve(to: curr, control1: cp1, control2: cp2)
                        }

                        // Draw Gradient Fill
                        var fillPath = linePath
                        fillPath.addLine(to: CGPoint(x: pointOffset(points.count - 1).x, y: chartBottom))
                        fillPath.addLine(to: CGPoint(x: pointOffset(0).x, y: chartBottom))
                        fillPath.closeSubpath()

                        context.fill(
                            fillPath,
                            with: .linearGradient(
                                Gradient(colors: [Color.scribblePrimary.opacity(0.12), Color.scribblePrimary.opacity(0)]),
                                startPoint: CGPoint(x: 0, y: chartTop),
                                endPoint: CGPoint(x: 0, y: chartBottom)
                            )
                        )

                        // Draw Line
                        context.stroke(
                            linePath,
                            with: .color(Color.scribblePrimary),
                            style: StrokeStyle(lineWidth: strokeWidth, lineCap: .round, lineJoin: .round)
                        )
                    }

                    // Draw Dots with Halo
                    for i in 0..<points.count {
                        let p = pointOffset(i)
                        context.fill(
                            Path(ellipseIn: CGRect(x: p.x - haloRadius, y: p.y - haloRadius, width: haloRadius * 2, height: haloRadius * 2)),
                            with: .color(Color.scribbleSurfaceContainerLow)
                        )
                        context.fill(
                            Path(ellipseIn: CGRect(x: p.x - dotRadius, y: p.y - dotRadius, width: dotRadius * 2, height: dotRadius * 2)),
                            with: .color(Color.scribblePrimary)
                        )
                    }

                    // Draw X-axis labels (Start, Mid, End)
                    let xLabelIndices = points.count <= 3
                        ? Array(0..<points.count)
                        : [0, points.count / 2, points.count - 1]

                    let dateFormatter = VolumeChart.dateFormatter

                    for i in xLabelIndices {
                        let p = pointOffset(i)
                        let text = Text(dateFormatter.string(from: points[i].date))
                            .font(labelFont)
                            .foregroundColor(.secondary)

                        context.draw(text, at: CGPoint(x: p.x, y: chartBottom + 12), anchor: .top)
                    }
                }
            }
        }
        .frame(height: 180)
        .padding(ScribbleFitSpacing.medium)
        .background(Color.scribbleSurfaceContainerLow)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .accessibilityIdentifier("volumeChart")
    }

    private func formatAxisValue(_ value: Float) -> String {
        if value >= 1_000_000 {
            return String(format: "%.1fM", value / 1_000_000)
        }
        if value >= 1000 {
            return String(format: "%.1fk", value / 1000)
        }
        return String(format: "%.0f", value)
    }
}
