# Photo Edge

Este projeto é um aplicativo Android feito para edição de imagens.

## Funções disponíveis

- Desenhar com pincel;
- Apagar;
- Borrar / Desfocar;
- Recortar;
- Distorcer perspectiva;
- Redimensionar e rotacionar;
- Espelhar imagem;
- Recortar;
- Edição com multiplas camadas;
- Importação e exportação de imagens.

## Instalação

Fazer uma cópia do projeto

 > git clone https://github.com/DouglasSnt/photo_edge.git

Abrir o projeto pelo Android Studio

> File -> Open -> Caminho do projeto -> photo_edge

No arquivo `Settings.gradle` deletar a linha `include ':opencv`

Fazer o download do OpenCV para android através do link

> https://opencv.org/releases/

No Android Studio, importar a pasta `sdk` do arquivo baixado

> File -> New -> Import Module

Após importar, renomear o módulo de `:sdk` para `:opencv`

No arquivo `Settings.gradle` renomear a linha `include ':sdk'` para`include ':opencv'`

Sincronizar arquivos

> File -> Sync Project With Gradle Files

Fazer o Build

> Build -> Make Project

## Time de desenvolvimento
[Douglas dos Santos Rocha](https://github.com/DouglasSnt)

## Nota
Este projeto é uma atribuição da universidade [Universidade São Francisco](https://www.usf.edu.br/).

## License
```
Copyright 2021 Douglas dos Santos Rocha

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
