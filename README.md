# photo_edge

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
