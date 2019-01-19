## Jak používat CraftLibs

```java
// Získání všech řádků tabulky
ArrayList<DBRow> rows = CraftLibs.getSqlManager().query('SELECT * FROM players');
for(DBRow row : rows){
    System.out.println(row.getString('nick') + ":" + row.getInt('coins'));
}

// Získání řádků na základě hodnoty
ArrayList<DBRow> rows = CraftLibs.getSqlManager().query('SELECT * FROM players WHERE nick=?', "iGniSsak");
...

// Vložení řádku do tabulky
CraftLibs.getSqlManager().query('INSERT INTO players(nick,coins) VALUES(?,?)', "iGniSsak", 5);
```

## Integrace CraftLibs do pluginu

#### Gradle

Plugin si musíte přesunou do složky pluginu do složky **libs/** (je na vás jak si ji nazvete, ale musíte si upravit gradle kód)

```
repositories {
    flatDir { dirs 'libs' }
}

dependencies {
    compileOnly name: 'CraftLibs'
}
```

#### Maven - NEVEDEME
