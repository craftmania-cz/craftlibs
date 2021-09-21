<br />
<p align="center">
      <h1 align="center">CraftLibs</h1>
</p>
<p align="center">
  <a href="https://java.com/">
    <img src="http://ForTheBadge.com/images/badges/made-with-java.svg" alt="Made with Java">
  </a>
</p>
<p align="center">
    Knihovna obsahující několik základních API pro potřeby CraftMania.cz
</p

## Obsah

* [Integrace do pluginu](#integrace-craftlibs-do-pluginu)
* [Databáze](#databáze)
* [Sentry](#sentry)
* [Plugin updater](#plugin-updater)

## Integrace CraftLibs do pluginu
K funkčnosti je mít potřeba nastavený **gradle.properties**. Více info v [dokumentaci](https://doc.clickup.com/p/h/2apgg-36/0c497027eb0cb4d).

```
maven {
  url "https://maven.pkg.github.com/craftmania-cz/craftlibs"
  credentials {
    username = GPR_USER
    password = GPR_TOKEN
  }
}

dependencies {
    compileOnly group: 'cz.craftmania.craftlibs', name: 'craftlibs', version: '1.4.0'
}
```

## Databáze
```java
// Získání všech řádků tabulky
CraftLibs.getSqlManager().query('SELECT * FROM players').thenAcceptAsync(result -> {
    for(DBRow row : rows){
        System.out.println(row.getString('nick') + ":" + row.getInt('coins'));
    }
});;


// Získání řádků na základě hodnoty
CraftLibs.getSqlManager().query('SELECT * FROM players WHERE nick=?', "iGniSsak").thenAcceptAsync(result -> {});


// Vložení řádku do tabulky
CraftLibs.getSqlManager().query('INSERT INTO players(nick,coins) VALUES(?,?)', "iGniSsak", 5);
```

## Sentry
```java
String dns = "...";
CraftSentry craftSentry = new CraftSentry(dsn);

try {
    ...
} catch (Exception exception) {
    craftSentry.sendException(exception);
}
```

## Plugin updater
V souboru `config.yml` v sekci `updater` vyplníme názvy pluginů, které se mají updatovat. Tyto pluginy pak nahrajeme do složky `plugins/CraftLibs/updater`. Při vypnutí/restartování serveru se pak pluginy nahradí novou verzí.
