-- Artistas
INSERT INTO
    artists (name)
VALUES
    ('Serj Tankian'),
    ('Mike Shinoda'),
    ('Michel Teló'),
    ('Guns N'' Roses');

-- Álbuns
INSERT INTO
    albums (title)
VALUES
    ('Harakiri'),
    ('Black Blooms'),
    ('The Rough Dog'),
    ('The Rising Tied'),
    ('Post Traumatic'),
    ('Post Traumatic EP'),
    ('Where''d You Go'),
    ('Bem Sertanejo'),
    ('Bem Sertanejo - O Show (Ao Vivo)'),
    ('Bem Sertanejo - (1ª Temporada) - EP'),
    ('Use Your Illusion I'),
    ('Use Your Illusion II'),
    ('Greatest Hits');

-- Relacionamentos artistas-albuns
INSERT INTO
    artist_album (artist_id, album_id)
SELECT
    (
        SELECT
            id
        FROM
            artists
        WHERE
            name = 'Serj Tankian'
    ),
    id
FROM
    albums
WHERE
    title IN ('Harakiri', 'Black Blooms', 'The Rough Dog');

INSERT INTO
    artist_album (artist_id, album_id)
SELECT
    (
        SELECT
            id
        FROM
            artists
        WHERE
            name = 'Mike Shinoda'
    ),
    id
FROM
    albums
WHERE
    title IN (
        'The Rising Tied',
        'Post Traumatic',
        'Post Traumatic EP',
        'Where''d You Go'
    );

INSERT INTO
    artist_album (artist_id, album_id)
SELECT
    (
        SELECT
            id
        FROM
            artists
        WHERE
            name = 'Michel Teló'
    ),
    id
FROM
    albums
WHERE
    title IN (
        'Bem Sertanejo',
        'Bem Sertanejo - O Show (Ao Vivo)',
        'Bem Sertanejo - (1ª Temporada) - EP'
    );

INSERT INTO
    artist_album (artist_id, album_id)
SELECT
    (
        SELECT
            id
        FROM
            artists
        WHERE
            name = 'Guns N'' Roses'
    ),
    id
FROM
    albums
WHERE
    title IN (
        'Use Your Illusion I',
        'Use Your Illusion II',
        'Greatest Hits'
    );