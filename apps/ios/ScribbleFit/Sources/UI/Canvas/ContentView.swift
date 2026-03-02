import SwiftUI

struct ContentView: View {
    var body: some View {
        VStack(spacing: 20) {
            Image(systemName: "pencil.line")
                .font(.system(size: 60))
                .foregroundStyle(.primary)
            
            Text("ScribbleFit")
                .font(.system(size: 34, weight: .bold, design: .rounded))
            
            Text("Intelligent Canvas Pending")
                .font(.system(size: 17, weight: .medium, design: .rounded))
                .foregroundStyle(.secondary)
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
