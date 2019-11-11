--
-- PostgreSQL database dump
--

-- Dumped from database version 12.0
-- Dumped by pg_dump version 12.0

-- Started on 2019-11-11 08:58:57

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2862 (class 1262 OID 16539)
-- Name: vipscase; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE vipscase WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'English_Sweden.1252' LC_CTYPE = 'English_Sweden.1252';


ALTER DATABASE vipscase OWNER TO postgres;

\connect vipscase

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2849 (class 0 OID 16541)
-- Dependencies: 202
-- Data for Name: customers; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.customers (id, customername, customerpass, email, lastname, streetname, postcode, city, birthyear) VALUES (44, 'test', 'test', 'mail@mail.com', 'lasttest', 'null', 12345, 'testcity', '1990-01-01');
INSERT INTO public.customers (id, customername, customerpass, email, lastname, streetname, postcode, city, birthyear) VALUES (45, 'test2', 'test2', 'mail2@mail.com', 'lasttest2', 'teststreet2', 123452, 'testcity2', '1990-01-02');
INSERT INTO public.customers (id, customername, customerpass, email, lastname, streetname, postcode, city, birthyear) VALUES (46, 'test3', 'test3', 'mail3@mail.com', 'lasttest3', 'teststreet3', 1234523, 'testcity3', '1990-01-03');
INSERT INTO public.customers (id, customername, customerpass, email, lastname, streetname, postcode, city, birthyear) VALUES (47, 'test4', 'test4', 'mail4@mail.com', 'lasttest4', 'teststreet4', 12345234, 'testcity4', '1990-01-04');
INSERT INTO public.customers (id, customername, customerpass, email, lastname, streetname, postcode, city, birthyear) VALUES (48, 'test5', 'test5', 'mail5@mail.com', 'lasttest5', 'teststreet5', 123452345, 'testcity5', '1990-01-05');


--
-- TOC entry 2851 (class 0 OID 16549)
-- Dependencies: 204
-- Data for Name: order_details; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.order_details (id, order_id, product_id, status) VALUES (8, 4, 8, 'pending payment');
INSERT INTO public.order_details (id, order_id, product_id, status) VALUES (9, 4, 9, 'pending payment');
INSERT INTO public.order_details (id, order_id, product_id, status) VALUES (12, 7, 8, 'pending payment');
INSERT INTO public.order_details (id, order_id, product_id, status) VALUES (13, 7, 9, 'pending payment');
INSERT INTO public.order_details (id, order_id, product_id, status) VALUES (14, 8, 10, 'pending payment');
INSERT INTO public.order_details (id, order_id, product_id, status) VALUES (15, 8, 9, 'pending payment');


--
-- TOC entry 2853 (class 0 OID 16557)
-- Dependencies: 206
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.orders (id, customer_id) VALUES (4, 44);
INSERT INTO public.orders (id, customer_id) VALUES (7, 48);
INSERT INTO public.orders (id, customer_id) VALUES (8, 48);


--
-- TOC entry 2855 (class 0 OID 16562)
-- Dependencies: 208
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.products (id, productname, productdescription, instock, price) VALUES (6, 'tröja', 'blå', 10, 100);
INSERT INTO public.products (id, productname, productdescription, instock, price) VALUES (7, 'byxa', 'blå', 10, 100);
INSERT INTO public.products (id, productname, productdescription, instock, price) VALUES (8, 'jacka', 'vit', 10, 100);
INSERT INTO public.products (id, productname, productdescription, instock, price) VALUES (9, 'vänstersko', 'vit', 10, 100);
INSERT INTO public.products (id, productname, productdescription, instock, price) VALUES (10, 'högersko', 'grå', 10, 100);


--
-- TOC entry 2867 (class 0 OID 0)
-- Dependencies: 203
-- Name: customers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.customers_id_seq', 48, true);


--
-- TOC entry 2868 (class 0 OID 0)
-- Dependencies: 205
-- Name: order_details_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.order_details_id_seq', 15, true);


--
-- TOC entry 2869 (class 0 OID 0)
-- Dependencies: 207
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.orders_id_seq', 8, true);


--
-- TOC entry 2870 (class 0 OID 0)
-- Dependencies: 209
-- Name: products_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.products_id_seq', 10, true);


-- Completed on 2019-11-11 08:58:59

--
-- PostgreSQL database dump complete
--

