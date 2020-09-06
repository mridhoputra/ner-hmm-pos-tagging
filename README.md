# Named-Entity Recognition pada Bahasa Indonesia menggunakan Metode Hidden Markov Model dan POS-Tagging

Project Tugas Akhir ini dibuat untuk menyelesaikan pendidikan Strata-1 pada jurusan Teknik Informatika, Universitas Sriwijaya.

Tugas Akhir yang telah disusun dapat dilihat pada website Repository Unsri yaitu https://repository.unsri.ac.id/33269/.

Project ini menggunakan Data Training dan Data Testing dari https://github.com/yusufsyaifudin/indonesia-ner, yang telah dimodifikasi.

## Dependency

Project ini menggunakan 2 library, yaitu library untuk proses Stemming https://github.com/sastrawi/sastrawi, dan library untuk proses POS-Tagging menggunakan API **IPOSTAgger** yang telah dibuat oleh Wicaksono dan Purwarianti (2010) pada penelitiannya yang berjudul "HMM Based Part-of-Speech Tagger for Bahasa Indonesia".

Library yang digunakan pada project ini dapat dilihat pada folder `dist/lib`, dengan nama jar yaitu `HMM-1.0.0.jar` dan `jsastrawi-all-0.1.jar`.

## Menjalankan Project

Project ini dijalankan menggunakan file JFrame yaitu `JFrameNER.java`, file ini terdapat pada folder `Boundary` didalam folder `src`.

Project ini memiliki 2 fase, yaitu fase pelatihan data dan fase pengujian data.

Fase pelatihan data akan menghasilkan beberapa file yaitu `wordlist.txt`, `nerlist.txt`, `transition.txt`, `emission.txt`, dan `emissionwf.txt`. File ini akan digunakan pada fase pengujian data.

File-file diatas sudah dibuat pada project ini, sehingga anda dapat langsung menggunakan fase pengujian data untuk mendapatkan hasil pelabelan NER oleh sistem.

### Fase Pelatihan Data

Pada fase ini, input yang digunakan adalah file pada folder `data` yaitu `data_train.txt`. Anda dapat menggunakan file txt lainnya dalam melakukan pelatihan data ini, dengan catatan, file txt yang digunakan menggunakan format NER yang sama yaitu:
```
/PER
/LOC
/ORG
/TIME
```
Label NER diberikan pada setiap kata di dalam teks.

### Fase Pengujian Data

Pada fase ini, input yang digunakan adalah file pada folder `data` yaitu `data_test.txt`. File `data_test.txt` disini adalah file yang sengaja diberi label NER secara manual, dengan tujuan untuk mengetahui akurasi yang didapatkan dari sistem ini. Anda dapat menggunakan file .txt lainnya dalam melakukan pengujian data ini, dan tidak menjadi masalah jika file .txt nya adalah file txt tanpa diberi label NER.
