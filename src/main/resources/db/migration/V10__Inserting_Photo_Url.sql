-- Atualizar os campos de photo_url para as 12 primeiras pessoas famosas
UPDATE person
SET photo_url = CASE 
    WHEN id =  1 THEN 'D:/SourceCode/SPRING BOOT/Curso_Erudio/db_person_images/01-AyrtonSenna.jpg'
    WHEN id =  2 THEN 'D:/SourceCode/SPRING BOOT/Curso_Erudio/db_person_images/02-JDVance'
    WHEN id =  3 THEN 'D:/SourceCode/SPRING BOOT/Curso_Erudio/db_person_images/03-DonalTrump'
    WHEN id =  4 THEN 'D:/SourceCode/SPRING BOOT/Curso_Erudio/db_person_images/04-JairBolsonaro'
    WHEN id =  5 THEN 'D:/SourceCode/SPRING BOOT/Curso_Erudio/db_person_images/05-MuhammadAli'
    WHEN id =  6 THEN 'D:/SourceCode/SPRING BOOT/Curso_Erudio/db_person_images/06-OlavoDeCarvalho'
    WHEN id =  7 THEN 'D:/SourceCode/SPRING BOOT/Curso_Erudio/db_person_images/07-NikolaTesla'
    WHEN id =  8 THEN 'D:/SourceCode/SPRING BOOT/Curso_Erudio/db_person_images/08-MargaretThatcher'
    WHEN id =  9 THEN 'D:/SourceCode/SPRING BOOT/Curso_Erudio/db_person_images/09-IsabelDeBragança'
    WHEN id = 10 THEN 'D:/SourceCode/SPRING BOOT/Curso_Erudio/db_person_images/10-MariaLeopoldinaHabsburg'
    WHEN id = 11 THEN 'D:/SourceCode/SPRING BOOT/Curso_Erudio/db_person_images/11-JoseBonifacio'
    WHEN id = 12 THEN 'D:/SourceCode/SPRING BOOT/Curso_Erudio/db_person_images/12-PedroI'
    WHEN id = 13 THEN 'D:/SourceCode/SPRING BOOT/Curso_Erudio/db_person_images/13-PedroII'
    ELSE photo_url
END
WHERE id <= 18;
