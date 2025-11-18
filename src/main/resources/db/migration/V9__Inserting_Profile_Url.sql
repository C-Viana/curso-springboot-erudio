UPDATE person
SET profile_url = CASE 
    WHEN id = 1 THEN 'https://en.wikipedia.org/wiki/Ayrton_Senna'
    WHEN id = 2 THEN 'https://en.wikipedia.org/wiki/JD_Vance'
    WHEN id = 3 THEN 'https://en.wikipedia.org/wiki/Donald_Trump'
    WHEN id = 4 THEN 'https://en.wikipedia.org/wiki/Jair_Bolsonaro'
    WHEN id = 5 THEN 'https://en.wikipedia.org/wiki/Muhammad_Ali'
    WHEN id = 6 THEN 'https://en.wikipedia.org/wiki/Olavo_de_Carvalho'
    WHEN id = 7 THEN 'https://en.wikipedia.org/wiki/Nikola_Tesla'
    WHEN id = 8 THEN 'https://en.wikipedia.org/wiki/Margaret_Thatcher'
    WHEN id = 9 THEN 'https://pt.wikipedia.org/wiki/Isabel,_Princesa_Imperial_do_Brasil'
    WHEN id = 10 THEN 'https://pt.wikipedia.org/wiki/Maria_Leopoldina_da_%C3%81ustria'
    WHEN id = 11 THEN 'https://pt.wikipedia.org/wiki/Jos%C3%A9_Bonif%C3%A1cio_de_Andrada_e_Silva'
    WHEN id = 12 THEN 'https://pt.wikipedia.org/wiki/Pedro_I_do_Brasil'
    WHEN id = 13 THEN 'https://pt.wikipedia.org/wiki/Pedro_II_do_Brasil'
    ELSE profile_url
END
WHERE id <= 18;