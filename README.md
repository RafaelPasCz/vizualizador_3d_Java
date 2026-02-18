# Renderizador 3D - Pipeline Alvy Ray Smith

Este projeto é uma implementação de um motor de renderização 3D (Software Renderer) desenvolvido em Java puro, sem o uso de bibliotecas gráficas externas (como OpenGL ou DirectX).

## Funcionalidades Implementadas

  Pipeline Gráfico: Baseado na arquitetura descrita por Alvy Ray Smith (Transformações Mundo → Câmera → Recorte → Tela).

  Modelos de Iluminação:

  Phong Shading: Interpolação de normais por pixel (per-pixel lighting) para reflexos especulares.

  Rasterização: Algoritmo Scanline com interpolação de arestas.

  Visibilidade: Algoritmo Z-Buffer para a renderização.

  Recorte (Clipping): Algoritmo Sutherland-Hodgman 3D.

  Primitivas: Suporte a Cubos e Luzes.

## Como Rodar

Certifique-se de ter o Java (JRE) instalado.

Se você já possui o arquivo .jar, execute no terminal:

    java -jar Modelador3D.jar

## Como Compilar

Para compilar o código fonte e gerar o .jar manualmente via linha de comando

    mkdir bin
    javac -d bin Main.java ui/*.java logica/*.java
    cd bin
    jar cfe ../Modelador3D.jar Main .

A interface permite manipular objetos e câmera. Use o teclado para transformações rápidas no objeto selecionado:

 * Setas (← → ↑ ↓): Translação nos eixos X e Y.

 *  Shift + Setas (↑ ↓): Translação no eixo Z (profundidade).

 * W / S: Rotação no eixo X.

 * A / D: Rotação no eixo Y.

 * Q / E: Rotação no eixo Z.
    
