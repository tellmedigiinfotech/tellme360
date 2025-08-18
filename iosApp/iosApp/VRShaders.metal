#include <metal_stdlib>
using namespace metal;

struct VertexIn {
    float3 position [[attribute(0)]];
    float2 texcoord [[attribute(1)]];
};

struct VertexOut {
    float4 position [[position]];
    float2 texcoord;
};

struct Uniforms {
    float4x4 mvpMatrix;
};

vertex VertexOut vr_vertex(VertexIn in [[stage_in]],
                           constant float4x4 &mvp [[buffer(2)]]) {
    VertexOut out;
    out.position = mvp * float4(in.position, 1.0);
    out.texcoord = in.texcoord;
    return out;
}

fragment float4 vr_fragment(VertexOut in [[stage_in]],
                            texture2d<float> tex [[texture(0)]]) {
    constexpr sampler s(address::clamp_to_edge, filter::linear);
    float4 color = tex.sample(s, in.texcoord);
    
    // Apply gamma correction for better VR display
    color.rgb = pow(color.rgb, 1.0/2.2);
    
    return color;
}

// Additional shader for distortion correction (future enhancement)
vertex VertexOut vr_distortion_vertex(VertexIn in [[stage_in]],
                                      constant float4x4 &mvp [[buffer(2)]]) {
    VertexOut out;
    out.position = mvp * float4(in.position, 1.0);
    out.texcoord = in.texcoord;
    return out;
}

fragment float4 vr_distortion_fragment(VertexOut in [[stage_in]],
                                       texture2d<float> tex [[texture(0)]]) {
    constexpr sampler s(address::clamp_to_edge, filter::linear);
    
    // Basic barrel distortion correction for VR lenses
    float2 center = float2(0.5, 0.5);
    float2 coord = in.texcoord - center;
    float radius = length(coord);
    
    // Distortion coefficients (can be tuned for specific VR headsets)
    float k1 = 0.22;
    float k2 = 0.24;
    
    float distortion = 1.0 + k1 * radius * radius + k2 * radius * radius * radius * radius;
    float2 distortedCoord = center + coord * distortion;
    
    // Sample texture with distortion correction
    if (distortedCoord.x >= 0.0 && distortedCoord.x <= 1.0 && 
        distortedCoord.y >= 0.0 && distortedCoord.y <= 1.0) {
        float4 color = tex.sample(s, distortedCoord);
        color.rgb = pow(color.rgb, 1.0/2.2); // Gamma correction
        return color;
    } else {
        return float4(0.0, 0.0, 0.0, 1.0); // Black outside valid area
    }
}


