package com.excitemike.bocus.ui.modifier

import android.graphics.RuntimeShader
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush

val noodleZoomShader = RuntimeShader(
    """
        uniform float2 resolution;
        uniform float time;

        // Source: @notargs https://twitter.com/notargs/status/1250468645030858753
        float f(vec3 p) {
            p.z -= time * 10.;
            float a = p.z * .1;
            p.xy *= mat2(cos(a), sin(a), -sin(a), cos(a));
            return .1 - length(cos(p.xy) + sin(p.yz));
        }
        
        half4 main(vec2 fragcoord) { 
            vec3 d = .5 - fragcoord.xy1 / resolution.y;
            vec3 p=vec3(0);
            for (int i = 0; i < 32; i++) {
              p += f(p) * d;
            }
            return ((sin(p) + vec3(2, 5, 12)) / length(p)).xyz1;
        }
    """
)

@Composable
fun Modifier.noodleZoom(): Modifier {
    val time = produceState(0f) {
        val start = withFrameMillis { it }
        while (true) {
            withInfiniteAnimationFrameMillis {
                value = (it - start) / 1000f
            }
        }
    }
    val brush = remember { ShaderBrush(noodleZoomShader) }
    return this.drawWithCache {
        noodleZoomShader.setFloatUniform("resolution", this.size.width, this.size.height)
        noodleZoomShader.setFloatUniform("time", time.value)
        onDrawBehind { drawRect(brush) }
    }
}

@Composable
fun ShaderTest(modifier: Modifier = Modifier) {
    val time = produceState(0f) {
        val start = withFrameMillis { it }
        while (true) {
            withInfiniteAnimationFrameMillis {
                value = (it - start) / 1000f
            }
        }
    }
    val brush = remember { ShaderBrush(noodleZoomShader) }
    Box(modifier = modifier.fillMaxSize().drawWithCache {
        noodleZoomShader.setFloatUniform("resolution", this.size.width, this.size.height)
        noodleZoomShader.setFloatUniform("time", time.value)
        onDrawBehind { drawRect(brush) }
    })
}