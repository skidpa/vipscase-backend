--
-- PostgreSQL database dump
--

-- Dumped from database version 12.0
-- Dumped by pg_dump version 12.0

-- Started on 2019-11-11 10:14:28

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
-- TOC entry 203 (class 1259 OID 16606)
-- Name: customers_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.customers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customers_id_seq OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 202 (class 1259 OID 16598)
-- Name: customers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customers (
    id bigint DEFAULT nextval('public.customers_id_seq'::regclass) NOT NULL,
    customername text,
    customerpass text,
    email text,
    lastname text,
    streetname text,
    postcode integer,
    city text,
    birthyear text
);


ALTER TABLE public.customers OWNER TO postgres;

--
-- TOC entry 2817 (class 0 OID 16598)
-- Dependencies: 202
-- Data for Name: customers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.customers (id, customername, customerpass, email, lastname, streetname, postcode, city, birthyear) FROM stdin;
1	test	ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff	mail@mail.com	lasttest	teststreet	123452345	testcity	1990-01-07
\.


--
-- TOC entry 2824 (class 0 OID 0)
-- Dependencies: 203
-- Name: customers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.customers_id_seq', 1, true);


--
-- TOC entry 2690 (class 2606 OID 16605)
-- Name: customers customers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT customers_pkey PRIMARY KEY (id);


-- Completed on 2019-11-11 10:14:28

--
-- PostgreSQL database dump complete
--

