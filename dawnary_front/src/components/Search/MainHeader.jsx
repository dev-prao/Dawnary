import { useState } from 'react';
import { Link } from 'react-router-dom';
import PropTypes from 'prop-types';
import filterStyles from './SearchCss/SearchFilter.module.css';
import barStyles from './SearchCss/SearchBar.module.css';

const MainHeader = ({ onSearch, onFilterChange }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const handleSearch = () => {
    onSearch(searchTerm);
  };

  return (
    <header>
      <input 
        type="text" 
        value={searchTerm} 
        onChange={(e) => setSearchTerm(e.target.value)} 
      />
      <button onClick={handleSearch}>검색</button>
      <Link to="/mainCalendar"><button>달력</button></Link>
      <Link to="/myPage"><button>마이페이지</button></Link>
      <hr/>
      <div className={barStyles.container}>
        <button className={filterStyles.buttons} onClick={() => onFilterChange("all")}>통합 검색</button>
        <button className={filterStyles.buttons} onClick={() => onFilterChange("users")}>유저 검색</button>
        <button className={filterStyles.buttons} onClick={() => onFilterChange("series")}>시리즈만</button>
      </div>
    </header>
  );
};

MainHeader.propTypes = {
  onSearch: PropTypes.func.isRequired,
  onFilterChange: PropTypes.func.isRequired,
};

export default MainHeader;
