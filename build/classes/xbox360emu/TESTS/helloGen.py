# Definición de la fuente 8x8
FONT_8X8 = {
    'H':[0b01000010,0b01000010,0b01000010,0b01111110,0b01000010,0b01000010,0b01000010,0],
    'E':[0b01111110,0b01000000,0b01000000,0b01111100,0b01000000,0b01000000,0b01111110,0],
    'L':[0b01000000,0b01000000,0b01000000,0b01000000,0b01000000,0b01000000,0b01111110,0],
    'O':[0b00111100,0b01000010,0b01000010,0b01000010,0b01000010,0b01000010,0b00111100,0],
    'W':[0b01000010,0b01000010,0b01000010,0b01011010,0b01011010,0b01100110,0b01000010,0],
    'R':[0b01111100,0b01000010,0b01000010,0b01111100,0b01000100,0b01000010,0b01000010,0],
    'D':[0b01111000,0b01000100,0b01000010,0b01000010,0b01000010,0b01000100,0b01111000,0],
    'A':[0b00110000,0b01001000,0b10000100,0b11111100,0b10000100,0b10000100,0b10000100,0],
    'M':[0b10000001,0b11000011,0b10100101,0b10011001,0b10000001,0b10000001,0b10000001,0],
    'U':[0b10000001,0b10000001,0b10000001,0b10000001,0b10000001,0b10000001,0b01111110,0],
    'N':[0b10000001,0b11000001,0b10100001,0b10010001,0b10001001,0b10000101,0b10000011,0],
    ' ':[0,0,0,0,0,0,0,0],
}

# Parámetros de pantalla
WIDTH, HEIGHT = 800, 600
BYTES_PER_PIXEL = 4
FRAMEBUFFER_SIZE = WIDTH * HEIGHT * BYTES_PER_PIXEL

CHAR_W, CHAR_H = 8, 8
txt = "HOLA MUNDO"
REG_W = len(txt) * CHAR_W
REG_H = CHAR_H

# Generar píxeles
pixel_data = []
for y in range(REG_H):
    for x in range(WIDTH):
        if x < REG_W:
            cx = x // CHAR_W
            ch = txt[cx]
            glyph = FONT_8X8.get(ch.upper(), [0] * 8)
            bit_idx = x % CHAR_W
            bit = (glyph[y] >> (7 - bit_idx)) & 1
            color = 0xFFFFFFFF if bit else 0x00000000
        else:
            color = 0x00000000
        pixel_data.append(color)

# Completar resto del framebuffer
remaining_pixels = WIDTH * (HEIGHT - REG_H)
pixel_data.extend([0x00000000] * remaining_pixels)

# Generar binario
binary = b''.join(c.to_bytes(4, 'little') for c in pixel_data)

# Guardar
output_path = "fb_800x600_hola.bin"
with open(output_path, "wb") as f:
    f.write(binary)

output_path
